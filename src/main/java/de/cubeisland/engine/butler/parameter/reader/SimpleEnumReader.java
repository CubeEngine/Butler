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
package de.cubeisland.engine.butler.parameter.reader;

import de.cubeisland.engine.butler.CommandInvocation;

/**
 * A Reader for UpperCased Enum names
 */
public class SimpleEnumReader implements ArgumentReader
{
    // TODO simpleEnumCompleter
    @Override
    @SuppressWarnings("unchecked")
    public Object read(Class type, CommandInvocation invocation) throws ReaderException
    {
        if (invocation.getManager().hasReader(type))
        {
            return invocation.getManager().read(type, type, invocation);
        }
        if (Enum.class.isAssignableFrom(type))
        {
            Enum<?>[] enumConstants = ((Class<? extends Enum<?>>)type).getEnumConstants();
            String token = invocation.currentToken().replace(" ", "_").toUpperCase();
            for (Enum<?> anEnum : enumConstants)
            {
                if (anEnum.name().equals(token))
                {
                    invocation.consume(1);
                    return anEnum;
                }
            }
            throw new ReaderException("Could not find \"" + invocation.currentToken() + "\" in Enum");
        }
        throw new UnsupportedOperationException();
    }
}
