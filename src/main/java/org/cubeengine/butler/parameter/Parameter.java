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
package org.cubeengine.butler.parameter;

import java.util.List;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.filter.Filter;
import org.cubeengine.butler.filter.Filters;
import org.cubeengine.butler.parameter.property.ValueReader;
import org.cubeengine.butler.parameter.reader.ArgumentReader;
import org.cubeengine.butler.property.PropertyHolder;

/**
 * The Base for Parameters with a Set of ParameterProperties
 * <p>This Parameter Supports the ValueReader Property, which provides an ArgumentReader Object to use instead of preregistered ones</p>
 */
public abstract class Parameter extends PropertyHolder
{
    public static final int INFINITE = -1;
    public static final int DEFAULT = 1;

    private final Class<?> type;
    private final Class<?> readerType;
    protected int greed;
    private Class<?> defaultProvider;

    protected Parameter(Class<?> type, Class<?> reader, int greed)
    {
        this.type = type;
        this.readerType = reader;
        this.greed = greed;
    }

    /**
     * Returns the Type of this parameters value
     *
     * @return the type of the value
     */
    public Class<?> getType()
    {
        return this.type;
    }

    /**
     * Returns the Type of the Reader for this parameter
     *
     * @return the reader type
     */
    public Class<?> getReaderType()
    {
        return this.readerType;
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
        Filters filters = this.valueFor(Filters.class);
        if (filters != null)
        {
            for (Filter filter : filters)
            {
                filter.run(invocation);
            }
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
    protected abstract boolean isPossible(CommandInvocation invocation);

    /**
     * Parses the Parameter with given invocation
     *  @param invocation the CommandInvocation
     * @param params
     * @param suggestions
     */
    public abstract void parse(CommandInvocation invocation, List<ParsedParameter> params, List<Parameter> suggestions);

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
    protected abstract List<String> getSuggestions(CommandInvocation invocation);

    /**
     * Parses this parameter using given CommandInvocation
     *
     * @param invocation the CommandInvocation
     *
     * @return the ParsedParameter
     */
    protected ParsedParameter parseValue(CommandInvocation invocation)
    {
        int consumed = invocation.consumed();
        ArgumentReader reader = this.valueFor(ValueReader.class);
        Object read;
        if (reader != null)
        {
            read = reader.read(this.type, invocation);
        }
        else
        {
            read = invocation.getManager().read(this, invocation);
        }
        return ParsedParameter.of(this, read, invocation.tokensSince(consumed));
    }

    public int getGreed()
    {
        return greed;
    }

    public Class<?> getDefaultProvider()
    {
        return defaultProvider;
    }

    public void setDefaultProvider(Class<?> defaultProvider)
    {
        this.defaultProvider = defaultProvider;
    }
}
