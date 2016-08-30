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

public class NamedParser extends SimpleParser
{
    public NamedParser(Parameter parameter, String[] names)
    {
        super(parameter);
        parameter.offer(Properties.NAMES, names);
        parameter.offer(Properties.GREED, parameter.getGreed() + 1); // add greed for name
    }

    @Override
    public void parse(CommandInvocation invocation, List<ParsedParameter> params, List<Parameter> suggestions)
    {
        String token = invocation.currentToken();
        for (String name : getNames())
        {
            if (name.equalsIgnoreCase(token))
            {
                invocation.consume(1); // TODO perhaps remember parsed name
                break;
            }
        }
        // do not parse value if suggestion and last token
        if (suggestions != null && (invocation.consumed() >= invocation.tokens().size() - 1))
        {
            return;
        }
        super.parse(invocation, params, suggestions);
    }

    public String[] getNames()
    {
        return parameter.getProperty(Properties.NAMES);
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        int tokensLeft = invocation.tokens().size() - invocation.consumed();
        if (tokensLeft == 1)
        {
            ArrayList<String> result = new ArrayList<>();
            String token = invocation.currentToken().toLowerCase();
            for (String name : getNames())
            {
                if (name.startsWith(token))
                {
                    result.add(name);
                }
            }
            return result;
        }
        try
        {
            this.parse(invocation, new ArrayList<ParsedParameter>(), new ArrayList<Parameter>());
        }
        catch (Exception ignored)
        {

        }
        return super.getSuggestions(invocation);
    }

    @Override
    public boolean isPossible(CommandInvocation invocation)
    {
        return isName(invocation.currentToken()) && super.isPossible(invocation);
    }

    private boolean isName(String token)
    {
        boolean isPossible = false;
        for (String name : getNames())
        {
            if (name.equalsIgnoreCase(token))
            {
                isPossible = true;
                break;
            }
        }
        return isPossible;
    }

    @Override
    public ParameterType getType()
    {
        return ParameterType.NAMED;
    }
}
