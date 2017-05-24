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
package org.cubeengine.butler.parameter;

import java.util.List;

/**
 * A parsed parameter
 */
public class ParsedParameter
{
    private final Parameter parameter;
    private final Object parsedValue;
    private final String rawValue;

    private ParsedParameter(Parameter parameter, Object parsedString, String rawValue)
    {
        this.parameter = parameter;
        this.parsedValue = parsedString;
        this.rawValue = rawValue;
    }

    /**
     * Creates an empty ParsedParameter
     *
     * @return an empty ParsedParameter
     */
    public static ParsedParameter empty()
    {
        return new ParsedParameter(null, "", "");
    }

    /**
     * Creates a ParsedParameter
     *
     * @param parameter the parameter
     * @param parsed    the parsed value
     * @param raw       the raw value
     * @param <T>       the Type of the parsed value
     *
     * @return the ParsedParameter
     */
    public static <T> ParsedParameter of(Parameter parameter, T parsed, String raw)
    {
        return new ParsedParameter(parameter, parsed, raw);
    }

    /**
     * Creates a ParsedParameter
     *
     * @param parameter the parameter
     * @param parsed    the parsed value
     *
     * @return the ParsedParameter
     */
    public static ParsedParameter of(Parameter parameter, List<ParsedParameter> parsed)
    {
        return new ParsedParameter(parameter, parsed, null);
    }

    /**
     * Returns the parameter
     *
     * @return the parameter
     */
    public Parameter getParameter()
    {
        return parameter;
    }

    /**
     * Returns the parsed value
     *
     * @return the parsed value
     */
    public Object getParsedValue()
    {
        return parsedValue;
    }

    /**
     * Returns the raw String or null if this is a group of Parameters
     *
     * @return the raw String
     */
    public String getRawValue()
    {
        return rawValue;
    }
}
