package org.cubeengine.butler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Character.isWhitespace;
import static java.util.Collections.emptyList;

public class Tokenizer
{
    public static final char ESCAPE = '\\';
    public static final char SINGLE_QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '"';

    public enum TokenType
    {
        PLAIN,
        QUOTED;
    }

    public static final class Token
    {
        public final TokenType type;
        public final String value;

        public Token(TokenType type, String value)
        {
            this.type = type;
            this.value = value;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (!(o instanceof Token))
            {
                return false;
            }
            final Token token = (Token)o;
            return type == token.type && Objects.equals(value, token.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(type, value);
        }

        @Override
        public String toString()
        {
            return "Token{" + "type=" + type + ", value='" + value + '\'' + '}';
        }
    }

    public static List<Token> tokenize(String input)
    {
        if (input.isEmpty())
        {
            return emptyList();
        }
        int i = 0;
        int len = input.length();
        char current;
        List<Token> out = new ArrayList<>();
        boolean expectPlain = false;

        while (i < len)
        {
            current = input.charAt(i);
            if (isWhitespace(current))
            {
                do
                {
                    current = input.charAt(++i);
                }
                while (isWhitespace(current));
            }
            else if (!expectPlain && (current == SINGLE_QUOTE || current == DOUBLE_QUOTE))
            {
                int start = i++;
                final char quote = current;
                boolean complete = false;
                while (i < len)
                {
                    current = input.charAt(i++);
                    if (current == quote)
                    {
                        complete = true;
                        out.add(new Token(TokenType.QUOTED, unescape(input, start, i, quote)));
                        break;
                    }
                    if (current == ESCAPE && i < len && input.charAt(i) == quote)
                    {
                        i++;
                    }
                }
                if (!complete)
                {
                    expectPlain = true;
                    i = start;
                }
            }
            else
            {
                expectPlain = false;
                int start = i++;
                while (i < len)
                {
                    current = input.charAt(i);
                    if (isWhitespace(current))
                    {
                        break;
                    }
                    i++;
                }
                out.add(new Token(TokenType.PLAIN, input.substring(start, i)));
            }
        }

        return out;
    }

    public static String unescape(String input, int start, int end, char quote)
    {
        StringBuilder out = new StringBuilder(end - start);
        char current, next;
        for (int i = start + 1; i < end; ++i)
        {
            current = input.charAt(i);
            if (current == ESCAPE && i + 1 < end)
            {
                next = input.charAt(++i);
                if (next == ESCAPE || next == quote)
                {
                    out.append(next);
                }
                else
                {
                    out.append(current).append(next);
                }
            }
            else
            {
                out.append(current);
            }
        }
        return out.deleteCharAt(out.length() - 1).toString();
    }
}
