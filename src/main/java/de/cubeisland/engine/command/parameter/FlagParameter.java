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

import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.parameter.property.FixedValues;
import de.cubeisland.engine.command.parameter.property.ValueReader;

public class FlagParameter extends Parameter
{
    public FlagParameter(String name, String longName)
    {
        super(Boolean.class, ValueReader.class);
        this.setProperty(new ValueReader(new FlagReader(name, longName))); // Set Custom FlagReader!
        this.setProperty(new FixedValues(new String[]{name, longName})); // Fixed Values
    }

    public final String name()
    {
        return this.valueFor(FixedValues.class)[0];
    }

    public final String longName()
    {
        return this.valueFor(FixedValues.class)[1];
    }

    @Override
    protected boolean accepts(CommandInvocation invocation)
    {
        String token = invocation.currentToken();
        if (token.startsWith("-"))
        {
            token = token.substring(1);
            for (String name : this.valueFor(FixedValues.class))
            {
                if (name.equalsIgnoreCase(token))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void parse(CommandInvocation invocation)
    {
        ParsedParameter parsedParameter = this.parseValue(invocation);
        if (parsedParameter.getParsedValue() == null)
        {
            throw new IllegalArgumentException("Invalid CommandCall! Flag should be validated but was not valid.");
        }
        invocation.valueFor(ParsedParameters.class).add(parsedParameter);
    }

    @Override
    protected List<String> getSuggestions(CommandInvocation invocation)
    {
        if (invocation.tokens().size() - invocation.consumed() == 1)
        {
            String token = invocation.currentToken();
            List<String> list = new ArrayList<>();
            if (token.startsWith("-") || token.isEmpty())
            {
                if (token.startsWith("-"))
                {
                    token = token.substring(1);
                }
                if (this.name().startsWith(token))
                {
                    list.add("-" + this.name());
                }
                if (this.longName().startsWith(token))
                {
                    list.add("-" + this.longName());
                }
            }
            return list;
        }
        if (this.accepts(invocation))
        {
            this.parse(invocation);
        }
        return null;
    }
}
