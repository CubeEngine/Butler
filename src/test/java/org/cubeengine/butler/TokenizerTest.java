/*
 * The MIT License
 * Copyright © 2014 Cube Island
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
package org.cubeengine.butler;

import org.cubeengine.butler.Tokenizer.Token;
import org.cubeengine.butler.Tokenizer.TokenType;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.cubeengine.butler.Tokenizer.tokenize;
import static org.junit.Assert.assertEquals;

public class TokenizerTest
{
    private static Token plain(String in)
    {
        return new Token(TokenType.PLAIN, in);
    }

    private static Token quoted(String in)
    {
        return new Token(TokenType.QUOTED, in);
    }

    @Test
    public void testTokenize()
    {
        assertEquals(asList(plain("a"), plain("b"), plain("c")), tokenize("a b c"));
        assertEquals(asList(plain("\\\"\\a"), plain("b\""), plain("\\\"c")), tokenize("\\\"\\a b\" \\\"c"));
        assertEquals(asList(quoted("a b "), plain("c")), tokenize("\"a b \"c"));
        assertEquals(asList(plain("a"), plain("b"), plain("c"), plain("")), tokenize("a b c "));
    }
}
