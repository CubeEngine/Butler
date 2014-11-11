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

import java.lang.reflect.InvocationTargetException;
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
import de.cubeisland.engine.command.methodic.MethodicCommandContainer;

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

        CommandBase replaced = this.commands.put(descriptor.getName().toLowerCase(), command);
        if (replaced != null)
        {
            // TODO replacement Policy
            // for now always replace
            // TODO replacement Policy if command instanceOf AliasCommand
        }
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
            // TODO check if allowed to run cmd / permission and type
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
        // TODO check if allowed to run
        /*

        public void checkContext(CommandContext ctx) throws CommandException
        {
            if (ctx.getCommand().isCheckperm() && !ctx.getCommand().isAuthorized(ctx.getSource()))
            {
                throw new PermissionDeniedException(ctx.getCommand().getPermission());
            }
            super.checkContext(ctx); // After general perm check -> check bounds etc.
            CtxDescriptor descriptor = ctx.getCommand().getContextFactory().descriptor();
            // TODO also check perm for indexed Parameters
            for (NamedParameter named : descriptor.getNamedGroups().listAll())
            {
                if (named instanceof PermissibleNamedParameter && ctx.hasNamed(named.getName()) &&
                    !((PermissibleNamedParameter)named).checkPermission(ctx.getSource()))
                {
                    throw new PermissionDeniedException(((PermissibleNamedParameter)named).getPermission());
                }
            }

            for (FlagParameter flag : descriptor.getFlags())
            {
                if (flag instanceof PermissibleFlag && ctx.hasFlag(flag.getName())
                    && !((PermissibleFlag)flag).checkPermission(ctx.getSource()))
                {
                    throw new PermissionDeniedException(((PermissibleFlag)flag).getPermission());
                }
            }
        }

         */
    }

    /**
     * Is called whenever an exception occurred while invoking this command
     * @param e the exception
     * @param invocation the invocation
     */
    protected void handleException(Throwable e, CommandInvocation invocation)
    {
        if (e instanceof InvocationTargetException)
        {
            e = e.getCause();
        }
        e.printStackTrace();
        // command is this hiw convenient
        // TODO CommandException handling via property
    }

    /**
     * Is called after no command could be found to dispatch
     *
     * @param invocation the invocation
     * @return whether the command ran successfully
     */
    protected boolean selfExecute(CommandInvocation invocation)
    {
        // TODO override in CE to get Help subcmd when empty args OR cmd not found to show possible sub-cmds (did you mean... ?) OR delegation
        // TODO delegation here
        return false;
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        List<String> tokens = invocation.tokens();
        List<String> result = new ArrayList<>();
        if (tokens.isEmpty())
        {
            for (CommandBase command : this.getCommands())
            {
                result.add(command.getDescriptor().getName());
            }
        }
        else if (tokens.size() == 1)
        {
            String curToken = invocation.currentToken().toLowerCase();
            for (String alias : this.commands.keySet())
            {
                if (alias.startsWith(curToken))
                {
                    result.add(StringUtils.join(" ", invocation.getLabels()) + " " + alias);
                }
            }
        }
        else
        {
            String curToken = invocation.currentToken();
            CommandBase command = this.getCommand(curToken);
            if (command == null)
            {
                return result; // Nothing to tab
            }
            return command.getSuggestions(invocation.subInvocation());
        }
        return result;

        /*
          public List<String> tabComplete(BaseCommandContext context)
    {
        if (context.last == ContextParser.Type.NOTHING)
        {
            return null;
        }
        final CtxDescriptor descriptor = this.getContextFactory().descriptor();
        if (context.last == PARAM_VALUE)
        {
            return tabCompleteParamValue(context, descriptor);
        }
        List<String> result = new ArrayList<>();
        List<String> args = context.getRawIndexed();
        String last = args.get(args.size() - 1);
        if (context.last == FLAG_OR_INDEXED)
        {
            tabCompleteFlags(context, descriptor, result, last);
            tabCompleteIndexed(context, descriptor, result, args.size() - 1, last);
        }
        else if (context.last == INDEXED_OR_PARAM)
        {
            tabCompleteIndexed(context, descriptor, result, args.size() - 1, last);
            tabCompleteParam(context, descriptor, result, last);
        }
        else if (context.last == ANY)
        {
            tabCompleteIndexed(context, descriptor, result, args.size() - 1, last);
            tabCompleteParam(context, descriptor, result, last);
            tabCompleteFlags(context, descriptor, result, last);
        }
        return result;
    }

    private List<String> tabCompleteParamValue(BaseCommandContext context, CtxDescriptor descriptor)
    {
        Iterator<Entry<String, String>> iterator = context.getRawNamed().entrySet().iterator();
        Entry<String, String> lastParameter;
        do
        {
            lastParameter = iterator.next();
        }
        while (iterator.hasNext());
        Completer completer = descriptor.getNamed(lastParameter.getKey()).getCompleter();
        if (completer != null)
        {
            return completer.complete(context, lastParameter.getValue());
        }
        return Collections.emptyList();
    }

    private void tabCompleteParam(BaseCommandContext context, CtxDescriptor descriptor, List<String> result, String last)
    {
        for (NamedParameter parameter : descriptor.getNamedGroups().listAll())
        {
            if (!context.hasNamed(parameter.getName()))
            {
                if (startsWithIgnoreCase(parameter.getName(), last))
                {
                    result.add(parameter.getName());
                }
                if (!last.isEmpty())
                {
                    for (String alias : parameter.getAliases())
                    {
                        if (alias.length() > 2 && startsWithIgnoreCase(alias, last))
                        {
                            result.add(alias);
                        }
                    }
                }
            }
        }
    }

    private void tabCompleteIndexed(BaseCommandContext context, CtxDescriptor descriptor, List<String> result, int index,
                                    String last)
    {
        IndexedParameter indexed = descriptor.getIndexed(index);
        if (indexed != null)
        {
            Completer indexedCompleter = indexed.getCompleter();
            if (indexedCompleter != null)
            {
                result.addAll(indexedCompleter.complete(context, last));
            }
        }
    }

    private void tabCompleteFlags(BaseCommandContext context, CtxDescriptor descriptor, List<String> result, String last)
    {
        if (!last.isEmpty())
        {
            last = last.substring(1);
        }
        for (FlagParameter commandFlag : descriptor.getFlags())
        {
            if (!context.hasFlag(commandFlag.getName()) && startsWithIgnoreCase(commandFlag.getLongName(), last))
            {
                result.add("-" + commandFlag.getLongName());
            }
        }
    }
         */
    }

    @Override
    public CommandDescriptor getDescriptor()
    {
        return this.descriptor;
    }
}
