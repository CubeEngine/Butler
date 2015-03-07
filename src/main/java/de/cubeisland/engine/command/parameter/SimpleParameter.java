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
import de.cubeisland.engine.command.completer.Completer;
import de.cubeisland.engine.command.completer.CompleterProperty;

/**
 * A Parameter implementation.
 */
public class SimpleParameter extends Parameter
{
    public SimpleParameter(Class<?> type, Class<?> reader, int greed)
    {
        super(type, reader, greed);
    }

    @Override
    public void parse(CommandInvocation invocation)
    {
        List<ParsedParameter> result = invocation.valueFor(ParsedParameters.class);
        ParsedParameter pParam = this.parseValue(invocation);
        if (!result.isEmpty() && result.get(result.size() - 1).getParameter().equals(pParam.getParameter()))
        {
            ParsedParameter last = result.remove(result.size() - 1);
            String joined = last.getParsedValue() + " " + pParam.getParsedValue();
            pParam = ParsedParameter.of(pParam.getParameter(), joined, joined);
        }
        result.add(pParam);
    }

    @Override
    protected boolean isPossible(CommandInvocation invocation)
    {
        // Just checking if the parameter is possible here
        int remainingTokens = invocation.tokens().size() - invocation.consumed();
        return remainingTokens >= 1 && remainingTokens >= greed;
    }

    @Override
    protected List<String> getSuggestions(CommandInvocation invocation)
    {
        List<String> result = new ArrayList<>();
        Class completerClass = this.valueFor(CompleterProperty.class);
        if (completerClass == null)
        {
            completerClass = this.getType();
        }

        Completer completer = invocation.getManager().getCompleter(completerClass);
        if (completer != null)
        {
            result.addAll(completer.getSuggestions(invocation));
        }
        else
        {
            System.out.println("No completer found for " + completerClass);
        }
        return result;
    }
}
