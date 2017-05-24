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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cubeengine.butler.alias.AliasCommand;
import org.cubeengine.butler.alias.AliasConfiguration;
import org.cubeengine.butler.exception.MissingCommandDescriptorException;
import org.cubeengine.butler.exception.UnhandledException;
import org.cubeengine.butler.filter.Filter;

/**
 * A Command that can dispatch sub-commands
 */
public class DispatcherCommand implements Dispatcher
{
    private final CommandDescriptor descriptor;

    private final Map<String, CommandBase> commands = new HashMap<>();

    public DispatcherCommand(CommandDescriptor descriptor)
    {
        if (descriptor == null)
        {
            throw new MissingCommandDescriptorException();
        }
        this.descriptor = descriptor;
    }

    @Override
    public boolean addCommand(CommandBase command)
    {
        CommandDescriptor descriptor = command.getDescriptor();

        if (!(descriptor instanceof Dispatchable))
        {
            throw new IllegalArgumentException("The given command is not dispatchable");
        }

        // Remove command from old dispatcher and set this one
        Dispatcher oldDispatcher = descriptor.getDispatcher();
        if (oldDispatcher != null)
        {
            oldDispatcher.removeCommand(command);
        }
        ((Dispatchable)descriptor).setDispatcher(this);

        this.commands.put(descriptor.getName().toLowerCase(), command);
        if (!(command instanceof AliasCommand))
        {

            for (AliasConfiguration alias : descriptor.getAliases())
            {
                if (alias.getDispatcher() == null)
                {
                    this.addCommand(new AliasCommand(alias, command));
                }
                else
                {
                    CommandBase aliasDispatcher = getManager().getCommand(alias.getDispatcher());
                    if (aliasDispatcher == null || !(aliasDispatcher instanceof Dispatcher))
                    {
                        throw new IllegalArgumentException("Cannot add alias to dispatcher! Command missing or is not a dispatcher.");
                    }
                    ((Dispatcher)aliasDispatcher).addCommand(new AliasCommand(alias, command));
                }
            }
        }

        if (command instanceof ContainerCommand)
        {
            ((ContainerCommand)command).registerSubCommands();
        }

        return true;
    }

    @Override
    public boolean removeCommand(CommandBase command)
    {
        boolean removed = this.commands.values().removeAll(Collections.singleton(command));
        if (removed)
        {
            ((Dispatchable)command.getDescriptor()).setDispatcher(null);
        }
        return removed;
    }

    @Override
    public Set<CommandBase> getCommands()
    {
        return Collections.unmodifiableSet(new HashSet<>(this.commands.values()));
    }

    @Override
    public boolean hasCommand(String alias)
    {
        return this.commands.containsKey(alias.toLowerCase());
    }

    @Override
    public CommandBase getCommand(String... alias)
    {
        if (alias.length == 0)
        {
            return this;
        }
        CommandBase cmd = this.commands.get(alias[0].toLowerCase());
        if (alias.length == 1)
        {
            return cmd;
        }
        if (cmd instanceof Dispatcher)
        {
            return ((Dispatcher)cmd).getCommand(Arrays.copyOfRange(alias, 1, alias.length));
        }
        return null;
    }

    @Override
    public final boolean execute(CommandInvocation invocation)
    {
        try
        {
            this.checkInvocation(invocation);
            if (!invocation.isConsumed())
            {
                CommandBase command = this.getCommand(invocation.currentToken());
                if (command != null)
                {
                    return command.execute(invocation.subInvocation(command));
                }
            }
            return this.selfExecute(invocation);
        }
        catch (Exception e)
        {
            this.handleException(e, invocation);
            return true;
        }
    }

    /**
     * Checks if given invocation is allowed to be executed
     *
     * @param invocation the invocation
     */
    protected void checkInvocation(CommandInvocation invocation)
    {
        if (getDescriptor() instanceof Filter)
        {
            ((Filter)getDescriptor()).run(invocation);
        }
    }

    /**
     * Is called whenever an exception occurred while invoking this command
     * @param e the exception
     * @param invocation the invocation
     */
    protected void handleException(Throwable e, CommandInvocation invocation)
    {
        if (!invocation.getManager().getExceptionHandler().handleException(e, this, invocation))
        {
            throw new UnhandledException(e);
        }
    }

    /**
     * Is called after no command could be found to dispatch
     *
     * @param invocation the invocation
     * @return whether the command ran successfully
     */
    protected boolean selfExecute(CommandInvocation invocation)
    {
        return false;
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        List<String> tokens = invocation.tokens();
        List<String> result = new ArrayList<>();
        if (invocation.isConsumed())
        {
            for (CommandBase command : this.getCommands())
            {
                result.add(command.getDescriptor().getName());
            }
        }
        else if (tokens.size() - invocation.consumed() == 1)
        {
            String curToken = invocation.currentToken().toLowerCase();
            for (String alias : this.commands.keySet())
            {
                if (alias.startsWith(curToken))
                {
                    result.add(alias);
                }
            }
        }
        else
        {
            String curToken = invocation.currentToken();
            CommandBase command = this.getCommand(curToken);
            if (command == null)
            {
                return null; // Nothing to tab
            }
            return command.getSuggestions(invocation.subInvocation(command));
        }
        return result;
    }

    @Override
    public CommandDescriptor getDescriptor()
    {
        return this.descriptor;
    }

    @Override
    public CommandManager getManager()
    {
        return getDescriptor().getDispatcher().getManager();
    }
}
