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

import java.util.Arrays;
import java.util.Iterator;

/**
 * String Utility Methods
 */
public class StringUtils
{
    /**
     * Joins the given Strings together using the delimiter
     *
     * @param delimiter the delimiter
     * @param strings the strings to join
     * @return the joined String
     */
    public static String join(String delimiter, Iterable<String> strings)
    {
        Iterator<String> it = strings.iterator();
        if (it.hasNext())
        {
            StringBuilder sb = new StringBuilder();
            sb.append(it.next());
            while (it.hasNext())
            {
                sb.append(delimiter).append(it.next());
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Joins the given Strings together using the delimiter
     *
     * @param delimiter the delimiter
     * @param strings the strings to join
     * @return the joined String
     */
    public static String join(String delimiter, String[] strings)
    {
        return join(delimiter, Arrays.asList(strings));
    }

    public static int parseString(StringBuilder sb, String[] args, int offset)
    {
        if (offset >= args.length) // Out of bounds?
        {
            return 1;
        }
        String fullToken = args[offset];
        if (fullToken.isEmpty()) // string is empty? consume token anyways.
        {
            return 1;
        }

        // first char is not a quote char? consume the String
        final char quoteChar = fullToken.charAt(0);
        if (quoteChar != '"' && quoteChar != '\'')
        {
            sb.append(fullToken);
            return 1;
        }

        // Handle single quoted String
        String string = fullToken.substring(1);
        // string has at least 2 chars and ends with the same quote char? return the string without quotes
        if (string.length() > 0 && string.charAt(string.length() - 1) == quoteChar)
        {
            sb.append(string.substring(0, string.length() - 1));
            return 1;
        }

        // Handle quoted Strings
        return parseQuotedString(sb, args, offset, quoteChar, string);
    }

    private static int parseQuotedString(StringBuilder sb, String[] args, int offset, char quoteChar, String string)
    {
        sb.append(string);
        offset++;
        int consumed = 1;

        while (offset < args.length)
        {
            sb.append(' ');
            consumed++;
            string = args[offset++];
            if (string.length() > 0 && string.charAt(string.length() - 1) == quoteChar)
            {
                sb.append(string.substring(0, string.length() - 1));
                break;
            }
            sb.append(string);
        }

        return consumed;
    }
}
