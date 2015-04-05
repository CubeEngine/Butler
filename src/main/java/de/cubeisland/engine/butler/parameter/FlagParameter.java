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
import java.util.List;
import de.cubeisland.engine.butler.CommandInvocation;
import de.cubeisland.engine.butler.parameter.property.Requirement;
import de.cubeisland.engine.butler.parameter.property.ValueReader;

public class FlagParameter extends Parameter
{
    private final String name;
    private final String longName;

    public FlagParameter(String name, String longName)
    {
        super(Boolean.class, ValueReader.class, DEFAULT);
        this.name = name;
        this.longName = longName;
        this.setProperty(new ValueReader(new FlagReader(name, longName))); // Set Custom FlagReader!
        this.setDefaultProvider(FlagReader.class);
        this.setProperty(Requirement.OPTIONAL);
    }

    public final String name()
    {
        return name;
    }

    public final String longName()
    {
        return longName;
    }

    @Override
    protected boolean isPossible(CommandInvocation invocation)
    {
        String token = invocation.currentToken();
        if (token.startsWith("-"))
        {
            token = token.substring(1);
            if (name.equalsIgnoreCase(token) || longName.equalsIgnoreCase(token))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void parse(CommandInvocation invocation)
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
        String token = invocation.tokens().get(invocation.tokens().size() - 1);
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
}
