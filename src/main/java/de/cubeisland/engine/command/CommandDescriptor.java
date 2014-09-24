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
package de.cubeisland.engine.command;

import java.util.Set;

import de.cubeisland.engine.command.property.Property;

/**
 * Provides general information about a command
 */
public interface CommandDescriptor
{
    /**
     * Returns the name of the command
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the aliases of the command
     *
     * @return the aliases
     */
    Set<String> getAliases();

    /**
     * Gets the usage of the command
     *
     * @param source the source to get the usage for
     *
     * @return the usage string
     */
    String getUsage(CommandSource source);

    /**
     * Returns the description of the command
     *
     * @return the description
     */
    String getDescription();

    /**
     * Returns the value of given property class
     *
     * @param clazz    the class
     * @param <ValueT> the Type of the value
     *
     * @return the value or null if not found
     */
    <ValueT> ValueT valueFor(Class<? extends Property<ValueT>> clazz);
}
