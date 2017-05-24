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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.parameter.ParameterParser.ParameterType;
import org.cubeengine.butler.parameter.property.Filters;
import org.cubeengine.butler.parameter.property.Properties;

/**
 * The Base for Parameters with a Set of ParameterProperties
 * <p>This Parameter Supports the ValueReader Property, which provides an ArgumentReader Object to use instead of preregistered ones</p>
 */
public class Parameter
{
    public static final int INFINITE = -1;
    public static final int DEFAULT = 1;


    private Map<Object, Object> properties = new HashMap<>();

    public Object offer(Object key, Object value)
    {
        return properties.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(Object key)
    {
        return ((T)properties.get(key));
    }

    /**
     * Returns the Type of this parameters value
     *
     * @return the type of the value
     */
    public Class<?> getType()
    {
        return getProperty(Properties.TYPE);
    }

    /**
     * Returns the Type of the Reader for this parameter
     *
     * @return the reader type
     */
    public Class<?> getReaderType()
    {
        return getProperty(Properties.READER);
    }

    /**
     * Checks if the parameter is allowed to be parsed for the given CommandInvocation
     *
     * @param invocation the invocation
     *
     * @return whether the parameter is allowed to be parsed
     */
    protected boolean isAllowed(CommandInvocation invocation)
    {
        Filters filters = this.getProperty(Properties.FILTERS);
        if (filters != null)
        {
            filters.run(invocation);
        }
        return true;
    }

    /**
     * Checks if the parameter is possible for the given CommandInvocation
     *
     * @param invocation the invocation
     *
     * @return whether the parameter can be parsed
     */
    protected boolean isPossible(CommandInvocation invocation)
    {
        return getParser().isPossible(invocation);
    }

    /**
     * Parses the Parameter with given invocation
     *  @param invocation the CommandInvocation
     * @param params
     * @param suggestions
     */
    public void parse(CommandInvocation invocation, List<ParsedParameter> params, List<Parameter> suggestions)
    {
        getParser().parse(invocation, params, suggestions);
    }

    public void parse(CommandInvocation invocation, List<ParsedParameter> params)
    {
        parse(invocation, params, null);
    }


    /**
     * Returns a List of suggested Strings
     *
     * @param invocation the CommandInvocation
     *
     * @return the suggestions
     */
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        return getParser().getSuggestions(invocation);
    }

    public int getGreed()
    {
        return getProperty(Properties.GREED);
    }

    public Class<?> getDefaultProvider()
    {
        return getProperty(Properties.DEFAULT_PROVIDER);
    }

    public ParameterType getParameterType()
    {
        return getParser().getType();
    }

    public ParameterParser getParser()
    {
        return getProperty(Properties.PARSER);
    }
}
