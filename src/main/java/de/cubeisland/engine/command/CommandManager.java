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

import java.util.Locale;

import de.cubeisland.engine.old.command.sender.ConsoleCommandSender;

/**
 * This class manages the registration of commands.
 */
public interface CommandManager
{
    void registerCommand(BaseCommand command, BaseCommand... parents);

    void registerCommands(CommandOwner owner, CommandHolder commandHolder, BaseCommand... parents);

    <T extends BaseCommand> void registerCommands(CommandOwner owner, Object commandHolder, Class<T> commandType,
                                                  BaseCommand... parents);

    BaseCommand getCommand(String name);

    void removeCommands(BaseCommand command);

    void removeCommands(Object owner);

    void removeCommands();

    boolean runCommand(BaseCommandSender sender, String commandLine);

    ConsoleCommandSender getConsoleSender();

    <T extends BaseCommand> void registerCommandFactory(CommandFactory<T> factory);

    void removeCommandFactory(Class<? extends BaseCommand> type);

    public <T extends BaseCommand> CommandFactory<T> getCommandFactory(Class<T> type);

    void logExecution(BaseCommandSender sender, BaseCommand cubeCommand, String[] args);

    void logFailed(BaseCommandSender sender, BaseCommand cubeCommand, String[] args);

    void logTabCompletion(BaseCommandSender sender, BaseCommand cubeCommand, String[] args);

    <T extends ResultManager> T getManager(Class<T> clazz);

    Locale getDefaultLocale();
}
