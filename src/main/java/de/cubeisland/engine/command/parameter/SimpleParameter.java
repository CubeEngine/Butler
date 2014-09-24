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

import java.util.List;

import de.cubeisland.engine.command.parameter.property.FixedValues;
import de.cubeisland.engine.command.parameter.property.Greed;
import de.cubeisland.engine.command.tokenized.TokenizedInvocation;

/**
 * A Parameter implementation.
 * <p>The following Properties are allowed</p>
 * <p>{@link Greed} the amount of tokens consumed by this parameter</p>
 * <p>{@link FixedValues} prefixed fixed values (e.g. used for named parameters) also augments the effective greed by one</p>
 */
public class SimpleParameter extends Parameter
{
    public SimpleParameter(Class<?> type, Class<?> reader)
    {
        super(type, reader);
        this.setProperty(Greed.DEFAULT);
    }

    @Override
    protected boolean parse(TokenizedInvocation call)
    {
        List<ParsedParameter> result = call.valueFor(ParsedParameters.class);
        String[] names = this.valueFor(FixedValues.class);
        if (names != null)
        {
            String name = call.currentToken().toLowerCase(); // previously matched in accepts(..)
            if (this.valueFor(Greed.class) == 0)
            {
                result.add(ParsedParameter.of(this, call.getManager().read(this, call), name));
                return true;
            }
            call.consume(1); // else consume name
            // TODO somehow include the name ?
        }
        ParsedParameter pParam = this.parseValue(call); // TODO handle greedy params better
        if (!result.isEmpty() && result.get(result.size() - 1).getParameter().equals(pParam.getParameter()))
        {
            ParsedParameter last = result.remove(result.size() - 1);
            String joined = last.getParsedValue() + " " + pParam.getParsedValue();
            pParam = ParsedParameter.of(pParam.getParameter(), joined, joined);
        }
        result.add(pParam);
        return true;
    }

    @Override
    protected boolean accepts(TokenizedInvocation call)
    {
        // Just checking if the parameter is possible here
        int greed = this.valueFor(Greed.class); // cannot be null as greed is preset if not set manually
        int remainingTokens = call.tokens().size() - call.consumed();
        String[] names = this.valueFor(FixedValues.class);
        if (names != null)
        {
            remainingTokens--; // the name consumes a token
            String lcToken = call.currentToken().toLowerCase();
            for (String name : names)
            {
                if (name.equals(lcToken))
                {
                    if (greed == 0 || remainingTokens > 1 && (remainingTokens >= greed))
                    {
                        return true;
                    }
                }
            }
            return false; // No match for named
        }
        // Non named:
        if (greed == 0 || remainingTokens >= 1 && (remainingTokens >= greed))
        {
            return true;
        }
        return false;
    }

}
