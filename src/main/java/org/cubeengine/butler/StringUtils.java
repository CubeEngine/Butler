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
}
