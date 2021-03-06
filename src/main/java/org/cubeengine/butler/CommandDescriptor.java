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

import java.util.List;
import org.cubeengine.butler.alias.AliasConfiguration;

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
     * Returns the aliases of the command under the same dispatcher
     *
     * @return the aliases
     */
    List<AliasConfiguration> getAliases();

    /**
     * Gets the usage of the command
     *
     * @param invocation the invocation to get the usage for
     * @param labels     the labels, if kept empty it will use the labels from the invocation
     *
     * @return the usage string
     */
    String getUsage(CommandInvocation invocation, String... labels);

    /**
     * Returns the description of the command
     *
     * @return the description
     */
    String getDescription();

    /**
     * Returns the dispatcher for the command of this descriptor
     *
     * @return the dispatcher
     */
    Dispatcher getDispatcher();

    /**
     * Returns the owner class
     *
     * @return the owner class
     */
    Class getOwner();
}
