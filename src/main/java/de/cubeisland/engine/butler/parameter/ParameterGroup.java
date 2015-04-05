/**
 * The MIT License
 * Copyright (c) 2014 Cube Island
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
package de.cubeisland.engine.butler.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import de.cubeisland.engine.butler.CommandException;
import de.cubeisland.engine.butler.CommandInvocation;
import de.cubeisland.engine.butler.parameter.property.FixedPosition;
import de.cubeisland.engine.butler.parameter.property.MethodIndex;
import de.cubeisland.engine.butler.parametric.SuggestionParameters;
import de.cubeisland.engine.butler.util.property.Property;

import static de.cubeisland.engine.butler.parameter.property.Requirement.isRequired;

/**
 * A ParameterGroup providing grouped Parameters
 */
public class ParameterGroup extends Parameter implements Property<ParameterGroup>
{
    private final List<Parameter> flags;
    private final List<Parameter> nonPositional;
    private final List<Parameter> positional;

    public ParameterGroup(List<Parameter> flags, List<Parameter> nonPositional, List<Parameter> positional)
    {
        super(null, null, 0); // TODO Type & Reader
        for (Parameter flag : flags) // Need to be FlagParameter
        {
            if (!(flag instanceof FlagParameter))
            {
                throw new IllegalArgumentException("Parameter is not a FlagParameter");
            }
        }
        for (Parameter namedParameter : nonPositional) // Need to have FixedValues but no
        {
            if (!(namedParameter instanceof NamedParameter))
            {
                throw new IllegalArgumentException("Non Positional Parameter has to be a named parameter");
            }
        }
        for (Parameter parameter : positional)
        {
            if (!parameter.hasProperty(FixedPosition.class))
            {
                throw new IllegalArgumentException("Positional Parameter has no position");
            }
        }

        this.flags = Collections.unmodifiableList(flags);
        this.nonPositional = Collections.unmodifiableList(nonPositional);
        this.positional = Collections.unmodifiableList(positional);
    }

    @Override
    public void parse(CommandInvocation invocation)
    {
        parse0(invocation, false);
    }

    private boolean parse0(CommandInvocation invocation, boolean suggestion)
    {
        List<Parameter> flags = new ArrayList<>(this.flags);
        List<Parameter> nonPositional = new ArrayList<>(this.nonPositional);
        List<Parameter> positional = new ArrayList<>(this.positional);
        Parameter greedy = null;

        List<ParsedParameter> params = invocation.valueFor(ParsedParameters.class);

        while (!invocation.isConsumed())
        {
            List<Parameter> suggestions = suggestParameters(invocation, positional, nonPositional, flags, greedy);

            if (suggestion && invocation.tokens().size() - invocation.consumed() == 1)
            {
                List<Parameter> list = invocation.valueFor(SuggestionParameters.class);
                list.addAll(suggestions); // Suggest indexed first then named then flags
                return true;
            }

            Collections.reverse(suggestions); // Try flags first then named then indexed
            boolean parsed = false;
            RuntimeException exception = null;
            int consumed = invocation.consumed();
            for (Parameter parameter : suggestions)
            {
                if (parameter.isPossible(invocation))
                {
                    if (suggestion && parameter instanceof NamedParameter && invocation.tokens().size() - consumed <= parameter.getGreed())
                    {
                        List<Parameter> list = invocation.valueFor(SuggestionParameters.class);
                        list.add(parameter);
                        return true;
                    }
                    try
                    {
                        parameter.parse(invocation);
                        parsed = true;
                        if (parameter.greed == INFINITE)
                        {
                            greedy = parameter;
                        }
                        flags.remove(parameter);
                        nonPositional.remove(parameter);
                        positional.remove(parameter);
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

        List<ParsedParameter> toGroup = new ArrayList<>();
        for (ParsedParameter param : params)
        {
            if (param.getParameter() != null) // empty param (last?)
            {
                if (!param.getParameter().hasProperty(MethodIndex.class))
                {
                    toGroup.add(param);
                }
            }
        }

        if (!toGroup.isEmpty())
        {
            // TODO create groupObject from toGroup and add to params
            // FieldHolder.class Property
        }

        for (Parameter parameter : positional)
        {
            if (parameter.getDefaultProvider() != null)
            {
                params.add(ParsedParameter.of(parameter, invocation.getManager().getDefault(parameter.getDefaultProvider(), invocation), null));
                continue;
            }
            if (isRequired(parameter) && !suggestion)
            {
                throw new TooFewArgumentsException();
            }
        }

        for (Parameter parameter : nonPositional)
        {
            if (parameter.getDefaultProvider() != null)
            {
                params.add(ParsedParameter.of(parameter, invocation.getManager().getDefault(parameter.getDefaultProvider(), invocation), null));
                continue;
            }
            if (isRequired(parameter) && parameter.greed != INFINITE)
            {
                if (!suggestion)
                {
                    throw new TooFewArgumentsException();
                }
            }
        }

        return true;
    }

    /**
     * Gets a list of possible parameters
     *  @param invocation    the invocation
     * @param positional    the positional parameters
     * @param nonPositional the non positional parameters
     * @param flags         the flags
     * @param greedy
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
    protected boolean isPossible(CommandInvocation invocation)
    {
        return true;
    }

    @Override
    public ParameterGroup value()
    {
        return this;
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        try
        {
            parse0(invocation, true);
        }
        catch (CommandException ignored)
        {
        }
        List<String> result = new ArrayList<>();
        for (Parameter parameter : invocation.valueFor(SuggestionParameters.class))
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
}
