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
package org.cubeengine.butler.parameter;

import java.util.ArrayList;
import java.util.List;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.parameter.property.Properties;
import org.cubeengine.butler.parameter.reader.ArgumentReader;

public class FlagParser implements ParameterParser
{
    private Parameter parameter;

    public FlagParser(Parameter parameter)
    {
        this.parameter = parameter;
    }

    public final String name()
    {
        return parameter.getProperty(Properties.FLAG_NAME);
    }

    public final String longName()
    {
        return parameter.getProperty(Properties.FLAG_LONGNAME);
    }

    @Override
    public boolean isPossible(CommandInvocation invocation)
    {
        String token = invocation.currentToken();
        if (token.startsWith("-"))
        {
            token = token.substring(1);
            if (name().equalsIgnoreCase(token) || longName().equalsIgnoreCase(token))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void parse(CommandInvocation invocation, List<ParsedParameter> params, List<Parameter> suggestions)
    {
        ParsedParameter parsedParameter = this.parseValue(invocation);
        if (parsedParameter.getParsedValue() == null)
        {
            throw new IllegalArgumentException("Invalid CommandCall! Flag should be validated but was not valid.");
        }
        params.add(parsedParameter);
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
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

    @Override
    public ParameterType getType()
    {
        return  ParameterType.FLAG;
    }

    protected ParsedParameter parseValue(CommandInvocation invocation)
    {
        int consumed = invocation.consumed();
        ArgumentReader reader = parameter.getProperty(Properties.VALUE_READER);
        Object read;
        if (reader != null)
        {
            read = reader.read(parameter.getType(), invocation);
        }
        else
        {
            read = invocation.getManager().read(parameter, invocation);
        }
        return ParsedParameter.of(parameter, read, invocation.tokensSince(consumed));
    }
}
