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
package de.cubeisland.engine.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.command.alias.AliasCommand;
import de.cubeisland.engine.command.alias.AliasConfiguration;
import de.cubeisland.engine.command.alias.Aliases;
import de.cubeisland.engine.command.filter.Filter;
import de.cubeisland.engine.command.filter.Filters;
import de.cubeisland.engine.command.methodic.MethodicCommandContainer;
import de.cubeisland.engine.command.util.property.PropertyHolder;

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

    protected DispatcherCommand()
    {
        if (this instanceof SelfDescribing)
        {
            this.descriptor = ((SelfDescribing)this).selfDescribe();
        }
        else
        {
            throw new MissingCommandDescriptorException();
        }
    }

    @Override
    public Dispatcher getBaseDispatcher()
    {
        Dispatcher dispatcher = this.descriptor.valueFor(DispatcherProperty.class).getDispatcher();
        if (dispatcher == null)
        {
            return this;
        }
        return dispatcher.getBaseDispatcher();
    }

    @Override
    public boolean addCommand(CommandBase command)
    {
        CommandDescriptor descriptor = command.getDescriptor();

        // Remove command from old dispatcher and set this one
        DispatcherProperty dispatcher = descriptor.valueFor(DispatcherProperty.class);
        if (dispatcher == null)
        {
            throw new IllegalArgumentException("The provided command is missing a DispatcherProperty");
        }
        Dispatcher oldDispatcher = dispatcher.getDispatcher();
        if (oldDispatcher != null)
        {
            oldDispatcher.removeCommand(command);
        }
        dispatcher.setDispatcher(this);

        this.commands.put(descriptor.getName().toLowerCase(), command);
        if (!(command instanceof AliasCommand))
        {
            for (AliasConfiguration alias : descriptor.valueFor(Aliases.class))
            {
                if (alias.getDispatcher() == null)
                {
                    this.addCommand(new AliasCommand(alias, command));
                }
                else
                {
                    CommandBase aliasDispatcher = this.getBaseDispatcher().getCommand(alias.getDispatcher());
                    if (aliasDispatcher == null || !(aliasDispatcher instanceof Dispatcher))
                    {
                        throw new IllegalArgumentException("Cannot add alias to dispatcher! Command missing or is not a dispatcher.");
                    }
                    ((Dispatcher)aliasDispatcher).addCommand(new AliasCommand(alias, command));
                }
            }
        }

        if (command.getDescriptor() instanceof PropertyHolder)
        {
            ((PropertyHolder)command.getDescriptor()).doFinalize();
        }

        if (command instanceof MethodicCommandContainer)
        {
            ((MethodicCommandContainer)command).registerSubCommands();
        }

        return true;
    }

    @Override
    public boolean removeCommand(CommandBase command)
    {
        boolean removed = this.commands.values().removeAll(Collections.singleton(command));
        if (removed)
        {
            DispatcherProperty dispatcher = descriptor.valueFor(DispatcherProperty.class);
            dispatcher.setDispatcher(null);
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
        if (alias.length != 0)
        {
            CommandBase cmd = this.commands.get(alias[0].toLowerCase());
            if (alias.length == 1)
            {
                return cmd;
            }
            if (cmd instanceof Dispatcher)
            {
                return ((Dispatcher)cmd).getCommand(Arrays.copyOfRange(alias, 1, alias.length));
            }
        }
        else
        {
            return this;
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
                    return command.execute(invocation.subInvocation());
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
        for (Filter filter : this.getDescriptor().valueFor(Filters.class))
        {
            filter.run(invocation);
        }
    }

    /**
     * Is called whenever an exception occurred while invoking this command
     * @param e the exception
     * @param invocation the invocation
     */
    protected void handleException(Throwable e, CommandInvocation invocation)
    {
        ExceptionHandler handler = this.getBaseDispatcher().getDescriptor().valueFor(ExceptionHandlerProperty.class);
        if (handler == null)
        {
            throw new MissingExceptionHandlerException("The Base Dispatcher has no Exception Handler!", e);
        }
        handler.handleException(e, this, invocation);
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
            return command.getSuggestions(invocation.subInvocation());
        }
        return result;
    }

    @Override
    public CommandDescriptor getDescriptor()
    {
        return this.descriptor;
    }
}
