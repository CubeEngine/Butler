/*
 * The MIT License
 * Copyright © 2014 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cubeengine.butler.parameter.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cubeengine.butler.exception.CommandException;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parameter.ParsedParameter;
import org.cubeengine.butler.parameter.ParsedParameters;
import org.cubeengine.butler.parameter.TooFewArgumentsException;
import org.cubeengine.butler.parameter.TooManyArgumentsException;
import org.cubeengine.butler.parameter.property.Properties;
import org.cubeengine.butler.parametric.Group;

import static org.cubeengine.butler.parameter.parser.ParameterParser.ParameterType.GROUP;
import static org.cubeengine.butler.parameter.parser.ParameterParser.ParameterType.NAMED;
import static org.cubeengine.butler.parameter.property.Requirement.isRequired;

/**
 * A ParameterGroup providing grouped Parameters
 */
public class GroupParser implements ParameterParser
{
    private final List<Parameter> flags;
    private final List<Parameter> nonPositional;
    private final List<Parameter> positional;

    private Parameter parameter;

    public GroupParser(Parameter parameter, Class<?> clazz, List<Parameter> parameters)
    {
        this.parameter = parameter;

        parameter.offer(Properties.GREED, 0);
        parameter.offer(Properties.TYPE, clazz);

        ArrayList<Parameter> flags = new ArrayList<>();
        ArrayList<Parameter> nonPositional = new ArrayList<>();
        ArrayList<Parameter> positional = new ArrayList<>();
        for (Parameter param : parameters)
        {
            switch (param.getParameterType())
            {
                case FLAG:
                    flags.add(param);
                    break;
                case INDEXED:
                    positional.add(param);
                    break;
                case NAMED:
                    nonPositional.add(param);
                    break;
                case GROUP:
                    positional.add(param); // TODO?
                    break;
            }
        }

        this.flags = Collections.unmodifiableList(flags);
        this.nonPositional = Collections.unmodifiableList(nonPositional);
        this.positional = Collections.unmodifiableList(positional);
    }

    @Override
    public void parse(CommandInvocation invocation, List<ParsedParameter> params, List<Parameter> suggs)
    {
        List<Parameter> flags = new ArrayList<>(this.flags);
        List<Parameter> nonPositional = new ArrayList<>(this.nonPositional);
        List<Parameter> positional = new ArrayList<>(this.positional);
        Parameter greedy = null;

        // Parse until invocation is consumed
        while (!invocation.isConsumed())
        {
            List<Parameter> suggestions = suggestParameters(invocation, positional, nonPositional, flags, greedy);

            if (suggs != null && invocation.tokens().size() - invocation.consumed() == 1)
            {
                suggs.addAll(suggestions); // Suggest indexed first then named then flags
                return;
            }

            Collections.reverse(suggestions); // Try flags first then named then indexed
            boolean parsed = false;
            RuntimeException exception = null;
            int consumed = invocation.consumed();
            for (Parameter parameter : suggestions)
            {
                if (parameter.isPossible(invocation))
                {
                    if (suggs != null && parameter.getParameterType() == NAMED
                        && invocation.tokens().size() - consumed <= parameter.getGreed())
                    {
                        suggs.add(parameter);
                        return;
                    }
                    try
                    {
                        List<ParsedParameter> newParams = new ArrayList<>();
                        if (parameter.getGreed() == Parameter.INFINITE)
                        {
                            if (!params.isEmpty())
                            {
                                int last = params.size() - 1;
                                if (params.get(last).getParameter() == parameter)
                                {
                                    newParams.add(params.remove(last));
                                }
                            }

                            greedy = parameter;
                        }
                        parameter.parse(invocation, newParams, suggs);
                        parsed = true;
                        flags.remove(parameter);
                        nonPositional.remove(parameter);
                        positional.remove(parameter);

                        if (newParams.size() == 1)
                        {
                            params.add(newParams.get(0));
                        }
                        else
                        {
                            Object parsedValue = newParams;
                            if (parameter.getType() != null && Group.class.isAssignableFrom(parameter.getType()))
                            {
                                parsedValue = readGroup(((Class<? extends Group>)parameter.getType()), newParams);
                            }
                            params.add(ParsedParameter.of(parameter, parsedValue, null));
                        }
                        break;
                    }
                    catch (RuntimeException e)
                    {
                        invocation.reset(consumed);
                        exception = e;
                    }
                }
            }
            if (!parsed)
            {
                if (exception == null)
                {
                    throw new TooManyArgumentsException();
                }
                else
                {
                    throw exception;
                }
            }
        }

        // Parse remaining parameters using default values or error out when too few arguments
        // First positional
        for (Parameter parameter : positional)
        {
            if (parameter.getDefaultProvider() != null)
            {
                params.add(ParsedParameter.of(parameter, invocation.providers().getDefault(parameter.getDefaultProvider(), invocation), null));
                continue;
            }
            if (isRequired(parameter) && suggs == null)
            {
                throw new TooFewArgumentsException();
            }
        }
        // then non-positional
        for (Parameter parameter : nonPositional)
        {
            if (parameter.getDefaultProvider() != null)
            {
                params.add(ParsedParameter.of(parameter, invocation.providers().getDefault(parameter.getDefaultProvider(), invocation), null));
                continue;
            }
            if (isRequired(parameter) && parameter.getGreed() != Parameter.INFINITE)
            {
                if (suggs == null)
                {
                    throw new TooFewArgumentsException();
                }
            }
        }
        // flags always default to false
    }

    private Group readGroup(Class<? extends Group> type, List<ParsedParameter> params)
    {
        try
        {
            Group group = type.newInstance();
            for (ParsedParameter param : params)
            {
                Field field = param.getParameter().getProperty(Properties.FIELD_HOLDER);
                field.set(group, param.getParsedValue());
            }
            return group;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Gets a list of possible parameters
     *
     * @param invocation    the invocation
     * @param positional    the positional parameters
     * @param nonPositional the non positional parameters
     * @param flags         the flags
     */
    private ArrayList<Parameter> suggestParameters(CommandInvocation invocation, List<Parameter> positional,
                                                   List<Parameter> nonPositional, List<Parameter> flags,
                                                   Parameter greedy)
    {
        ArrayList<Parameter> suggestions = new ArrayList<>();
        if (greedy != null && greedy.isAllowed(invocation))
        {
            suggestions.add(greedy);
        }
        if (!positional.isEmpty())
        {
            if (positional.get(0).isAllowed(invocation))
            {
                suggestions.add(positional.get(0));
            }
        }
        for (Parameter parameter : nonPositional)
        {
            if (parameter.isAllowed(invocation))
            {
                suggestions.add(parameter);
            }
        }
        for (Parameter flag : flags)
        {
            if (flag.isAllowed(invocation))
            {
                suggestions.add(flag);
            }
        }
        return suggestions;
    }

    @Override
    public boolean isPossible(CommandInvocation invocation)
    {
        return true;
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        List<Parameter> suggs = new ArrayList<>();
        try
        {
            ParsedParameters parsed = new ParsedParameters();
            invocation.setProperty(parsed);
            parse(invocation, parsed.value(), suggs);
        }
        catch (CommandException ignored)
        {
        }
        List<String> result = new ArrayList<>();
        for (Parameter parameter : suggs)
        {
            result.addAll(parameter.getSuggestions(invocation));
        }
        return result;
    }

    public List<Parameter> getFlags()
    {
        return flags;
    }

    public List<Parameter> getNonPositional()
    {
        return nonPositional;
    }

    public List<Parameter> getPositional()
    {
        return positional;
    }

    @Override
    public ParameterType getType()
    {
        return GROUP;
    }
}
