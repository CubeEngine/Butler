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
    }
}
