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

import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommandManager implements CommandManager
{
    private final Map<Class<? extends BaseCommand>, CommandFactory> commandFactories = new HashMap<Class<? extends BaseCommand>, CommandFactory>();

    @Override
    public <T extends BaseCommand> void registerCommandFactory(CommandFactory<T> factory)
    {
        this.commandFactories.put(factory.getCommandType(), factory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseCommand> CommandFactory<T> getCommandFactory(Class<T> type)
    {
        return (CommandFactory<T>)this.commandFactories.get(type);
    }

    @Override
    public void removeCommandFactory(Class<? extends BaseCommand> cmdType)
    {
        this.commandFactories.remove(cmdType);
    }

    @Override
    public <T extends BaseCommand> void registerCommands(CommandOwner owner, Object commandHolder, Class<T> commandType,
                                                         BaseCommand... parents)
    {
        CommandFactory<T> commandFactory = this.getCommandFactory(commandType);
        if (commandFactory == null)
        {
            throw new IllegalArgumentException("There is no CommandFactory for " + commandType.getName());
        }
        for (BaseCommand command : commandFactory.parseCommands(this, owner, commandHolder))
        {
            this.registerCommand(command, parents);
        }
    }

    @Override
    public void registerCommand(BaseCommand command, BaseCommand... parents)
    {
        if (command.isRegistered())
        {
            throw new IllegalArgumentException("The given command is already registered!");
        }
        command.getContextFactory().calculateArgBounds();
        if (parents.length != 0)
        {
            for (BaseCommand parent : parents)
            {
                parent.addChild(command);
            }
        }
        else
        {
            this.registerCommand0(command);
        }
        if (command instanceof CommandHolder)
        {
            this.registerCommands(command.getOwner(), (CommandHolder)command, command);
        }
    }

    @Override
    public void registerCommands(CommandOwner owner, CommandHolder commandHolder, BaseCommand... parents)
    {
        this.registerCommands(owner, commandHolder, commandHolder.getCommandType(), parents);
    }

    protected abstract void registerCommand0(BaseCommand command); // TODO register perm
}
