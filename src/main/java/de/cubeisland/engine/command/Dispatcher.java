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

/**
 * Handles command dispatching
 */
public interface Dispatcher extends CommandBase
{
    /**
     * Adds a command to this dispatcher
     *
     * @param command the command to add
     *
     * @return true if the command is now registered
     */
    boolean addCommand(CommandBase command);
    // TODO ReplacementPolicy

    /**
     * Removes a command from this dispatcher
     *
     * @param command the command to remove
     *
     * @return true if the command was removed
     */
    boolean removeCommand(CommandBase command);

    /**
     * Returns all the commands added to this dispatcher
     *
     * @return the added commands
     */
    Set<CommandBase> getCommands();

    /**
     * Returns whether this dispatcher has a command with given alias
     *
     * @param alias the alias
     *
     * @return true if a command was added for given alias
     */
    boolean hasCommand(String alias);

    /**
     * Returns the command for given optional parents and alias.
     * This will return null if any parent or alias is not found.
     * If no alias is given this will return itself.
     *
     * @param alias zero or more parents and one alias
     *
     * @return the command for given alias or null if not found
     */
    CommandBase getCommand(String... alias);

    /**
     * Returns the base dispatcher that being the first dispatcher that gets called
     *
     * @return the base dispatcher
     */
    Dispatcher getBaseDispatcher();
}
