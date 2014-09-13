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

import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.PropertyHolder;
import de.cubeisland.engine.command.parameter.property.Required;

/**
 * The Base for Parameters with a Set of ParameterProperties
 */
public abstract class Parameter extends PropertyHolder
{
    protected Parameter()
    {
        this.setProperty(Required.REQUIRED);
    }

    /**
     * Checks if the parameter is applicable to the current CommandCall
     *
     * @param call the CommandCall
     *
     * @return whether the parameter can be parsed
     */
    protected abstract boolean accepts(CommandCall call);

    /**
     * Is called after #parse is called but only when accepted prior
     *
     * @param call the CommandCall
     *
     * @return the parsed parameter
     */
    protected abstract boolean parse(CommandCall call);

    public final boolean parseParameter(CommandCall call)
    {
        return this.accepts(call) && this.parse(call);
    }

    //TODO  Static Reader ? replace them with named param with no consuming / caution when parsing we'll need to map to alias name not actual name!
    //TODO completer ?
}
