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

import de.cubeisland.engine.command.tokenized.TokenizedInvocation;
import de.cubeisland.engine.command.property.PropertyHolder;
import de.cubeisland.engine.command.parameter.property.Required;
import de.cubeisland.engine.command.parameter.property.ValueReader;
import de.cubeisland.engine.command.parameter.reader.ArgumentReader;

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
     * Checks if the parameter is applicable to the current CommandCall
     *
     * @param call the CommandCall
     * @return whether the parameter can be parsed
     */
    protected abstract boolean accepts(TokenizedInvocation call);

    /**
     * Is called after #parse is called but only when accepted prior
     *
     * @param call the CommandCall
     * @return the parsed parameter
     */
    protected abstract boolean parse(TokenizedInvocation call);

    /**
     * Tries to consume tokens of the CommandCall and parse this parameter
     *
     * @param call the CommandCall
     * @return whether tokens were consumed
     */
    public final boolean parseParameter(TokenizedInvocation call)
    {
        return this.accepts(call) && this.parse(call);
    }

    /**
     * Parses this parameter using given CommandCall
     *
     * @param call the CommandCall
     * @return the ParsedParameter
     */
    protected ParsedParameter parseValue(TokenizedInvocation call)
    {
        int consumed = call.consumed();
        ArgumentReader reader = call.valueFor(ValueReader.class);
        Object read;
        if (reader != null)
        {
            read = reader.read(call.getManager(), this.type, call);
        }
        else
        {
            read = call.getManager().read(this, call);
        }
        return ParsedParameter.of(this, read, call.tokensSince(consumed));
    }

    //TODO  Static Reader ? replace them with named param with no consuming / caution when parsing we'll need to map to alias name not actual name!
    //TODO completer ?
}
