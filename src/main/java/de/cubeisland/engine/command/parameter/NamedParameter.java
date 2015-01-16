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
package de.cubeisland.engine.command.parameter;

import java.util.ArrayList;
import java.util.List;
import de.cubeisland.engine.command.CommandInvocation;

public class NamedParameter extends SimpleParameter
{
    private String[] names;

    public NamedParameter(Class<?> clazz, Class<?> reader, String[] names, int greed)
    {
        super(clazz, reader, greed);
        this.names = names;
        this.greed++; // add greed for name
    }

    @Override
    public void parse(CommandInvocation invocation)
    {
        parse0(invocation, false);
    }

    private void parse0(CommandInvocation invocation, boolean suggestion)
    {
        String token = invocation.currentToken();
        for (String name : names)
        {
            if (name.equalsIgnoreCase(token))
            {
                invocation.consume(1); // TODO perhaps remember parsed name
                break;
            }
        }
        if (suggestion)
        {
            return;
        }
        super.parse(invocation);
    }

    @Override
    protected List<String> getSuggestions(CommandInvocation invocation)
    {
        int tokensLeft = invocation.tokens().size() - invocation.consumed();
        if (tokensLeft == 1)
        {
            ArrayList<String> result = new ArrayList<>();
            String token = invocation.currentToken().toLowerCase();
            for (String name : names)
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
            this.parse0(invocation, true);
        }
        catch (Exception ignored)
        {

        }
        return super.getSuggestions(invocation);
    }

    @Override
    protected boolean isPossible(CommandInvocation invocation)
    {
        return isName(invocation.currentToken()) && super.isPossible(invocation);
    }

    private boolean isName(String token)
    {
        boolean isPossible = false;
        for (String name : names)
        {
            if (name.equalsIgnoreCase(token))
            {
                isPossible = true;
                break;
            }
        }
        return isPossible;
    }

    public String[] getNames()
    {
        return this.names;
    }
}
