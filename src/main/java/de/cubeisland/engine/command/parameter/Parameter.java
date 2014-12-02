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

import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.parameter.property.Required;
import de.cubeisland.engine.command.parameter.property.ValueReader;
import de.cubeisland.engine.command.parameter.reader.ArgumentReader;
import de.cubeisland.engine.command.util.property.PropertyHolder;

/**
 * The Base for Parameters with a Set of ParameterProperties
 * <p>This Parameter Supports the ValueReader Property, which provides an ArgumentReader Object to use instead of preregistered ones</p>
 */
public abstract class Parameter extends PropertyHolder
{
    private final Class<?> type;
    private final Class<?> readerType;

    protected Parameter(Class<?> type, Class<?> reader)
    {
        this.type = type;
        this.readerType = reader;
        this.setProperty(Required.REQUIRED);
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
     * Checks if the parameter is applicable to the current CommandInvocation
     *
     * @param invocation the CommandInvocation
     *
     * @return whether the parameter can be parsed
     */
    // TODO also add permission checks etc. (hide the fact that the command "could" be correct but only permissions are missing)
    protected abstract boolean accepts(CommandInvocation invocation);

    /**
     * Is called after #parse is called but only when accepted prior
     *
     * @param invocation the CommandInvocation
     *
     * @return the parsed parameter
     */
    protected abstract void parse(CommandInvocation invocation);

    /**
     * Returns a List of suggested Strings
     *
     * @param invocation the CommandInvocation
     *
     * @return the suggestions
     */
    protected abstract List<String> getSuggestions(CommandInvocation invocation);

    /**
     * Tries to consume tokens of the CommandInvocation and parse this parameter
     *
     * @param invocation the CommandInvocation
     *
     * @return whether tokens were consumed
     */
    public final boolean parseParameter(CommandInvocation invocation)
    {
        if (this.accepts(invocation))
        {
            this.parse(invocation);
            return true;
        }
        return false;
    }

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
            read = reader.read(invocation.getManager(), this.type, invocation);
        }
        else
        {
            read = invocation.getManager().read(this, invocation);
        }
        return ParsedParameter.of(this, read, invocation.tokensSince(consumed));
    }

    //TODO  Static Reader ? replace them with named param with no consuming / caution when parsing we'll need to map to alias name not actual name!
    //TODO completer ?
}
