/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anselm Brehme, Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cubeisland.engine.command.parameter;

import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.CommandException;
import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.parameter.property.Greed;
import de.cubeisland.engine.command.parameter.property.MethodIndex;
import de.cubeisland.engine.command.parameter.property.Required;
import de.cubeisland.engine.command.parameter.property.group.FlagGroup;
import de.cubeisland.engine.command.parameter.property.group.NonPositionalGroup;
import de.cubeisland.engine.command.parameter.property.group.PositionalGroup;
import de.cubeisland.engine.command.util.property.Property;

import static de.cubeisland.engine.command.parameter.property.Greed.INFINITE_GREED;

/**
 * A ParameterGroup providing grouped Parameters
 */
public class ParameterGroup extends Parameter implements Property<ParameterGroup>
{
    public ParameterGroup(List<Parameter> flags, List<Parameter> nonPositional, List<Parameter> positional)
    {
        super(null, null); // TODO Type & Reader
        this.setProperty(new FlagGroup(flags));
        this.setProperty(new NonPositionalGroup(nonPositional));
        this.setProperty(new PositionalGroup(positional));
    }

    @Override
    public boolean accepts(CommandInvocation invocation)
    {
        return true;
    }

    @Override
    protected void parse(CommandInvocation invocation)
    {
        parse0(invocation, false);
    }

    private boolean parse0(CommandInvocation invocation, boolean suggestion)
    {
        List<Parameter> flags = new ArrayList<>(this.valueFor(FlagGroup.class));
        List<Parameter> nonPositional = new ArrayList<>(this.valueFor(NonPositionalGroup.class));
        List<Parameter> positional = new ArrayList<>(this.valueFor(PositionalGroup.class));

        List<ParsedParameter> params = invocation.valueFor(ParsedParameters.class);

        while (!invocation.isConsumed())
        {
            if (suggestion && invocation.tokens().size() - invocation.consumed() == 1)
            {
                break;
            }
            if (invocation.currentToken().isEmpty())
            {
                invocation.consume(1); // ignore empty args
                if (invocation.consumed() == invocation.tokens().size()) // except last
                {
                    params.add(ParsedParameter.empty());
                }
            }
            else
            {
                if (!this.parseMatching(invocation, flags, false)
                 && !this.parseMatching(invocation, nonPositional, false)
                 && !this.parseMatching(invocation, positional, true))
                {
                    if (!suggestion)
                    {
                        throw new TooManyArgumentsException();
                    }
                }
            }
        }

        // TODO perhaps here squash greedy params at the end together in first pass

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
            if (parameter.valueFor(Required.class) && parameter.valueFor(Greed.class)
                != INFINITE_GREED) // TODO infinite greed better
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
     * Creates a parsed Parameter for the current token
     *
     * @param invocation the CommandInvocation
     * @param searchList the list to search the parameter in
     * @param positonal  whether the list is positional or not
     *
     * @return the parsed parameter or null if not applicable
     */
    private boolean parseMatching(CommandInvocation invocation, List<Parameter> searchList, boolean positonal)
    {
        boolean parsed = false;
        Parameter toRemove = null;
        for (Parameter parameter : searchList)
        {
            toRemove = parameter;
            parsed = parameter.parseParameter(invocation);
            if (parsed)
            {
                break;
            }
            if (positonal && parameter.valueFor(Required.class))
            {
                break;
            }
        }
        if (parsed && !Greed.isInfinite(toRemove.valueFor(Greed.class))) // TODO handle greedy param better
        {
            searchList.remove(toRemove); // No reuse
        }
        return parsed;
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
        {}
        // TODO get all parameter that has not consumed tokens and try to get suggestions from it
        System.out.println("Tab: " + invocation.currentToken());
        return null;
    }
}
