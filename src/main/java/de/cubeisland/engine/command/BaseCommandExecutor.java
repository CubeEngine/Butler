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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

import de.cubeisland.engine.command.ContainerCommand.DelegatingContextFilter;

import static de.cubeisland.engine.command.StringUtils.startsWithIgnoreCase;

public abstract class BaseCommandExecutor
{
    private final BaseCommand command;

    public BaseCommandExecutor(BaseCommand command)
    {
        this.command = command;
    }

    public void onCommand(BaseCommandSender sender, String label, String[] args)
    {
        CommandContext ctx = null;
        try
        {
            ctx = toCommandContext(this.command, sender, label, args, false);

            // TODO async cmds

            // sync call:
            ctx.runAndShowResult();
        }
        catch (Exception e)
        {
            this.handleCommandException(ctx, sender, e);
        }
    }

    public List<String> onTabComplete(BaseCommandSender sender, String label, String[] args)
    {
        CommandContext ctx = null;
        try
        {
            ctx = toCommandContext(this.command, sender, label, args, true);

            List<String> result = this.completeChild(ctx);
            if (result == null)
            {
                result = ctx.getCommand().tabComplete(ctx);
            }

            if (result != null)
            {
                // TODO configurable max offers
                return result;
            }
        }
        catch (Exception e)
        {
            this.handleCommandException(ctx, sender, e);
        }
        return Collections.emptyList();
    }

    protected final List<String> completeChild(CommandContext context)
    {
        BaseCommand command = context.getCommand();
        if (command.hasChildren() && context.getRawIndexed().size() == 1)
        {
            List<String> actions = new ArrayList<String>();
            String token = context.getString(0).toLowerCase(Locale.ENGLISH);

            BaseCommandSender sender = context.getSender();
            Set<BaseCommand> names = command.getChildren();
            for (BaseCommand child : names)
            {
                if (startsWithIgnoreCase(child.getName(), token) && child.isAuthorized(sender))
                {
                    actions.add(child.getName());
                }
            }
            Collections.sort(actions, String.CASE_INSENSITIVE_ORDER);

            return actions;
        }
        return null;
    }

    private static CommandContext toCommandContext(BaseCommand command, BaseCommandSender sender, String label, String[] args, boolean tabComplete)
    {
        Stack<String> labels = new Stack<String>();
        labels.push(label);

        if (args.length > 0 && !args[0].isEmpty())
        {
            while (args.length > 0)
            {
                if ("?".equals(args[0]))
                {
                    new CommandContext(command, sender, labels, Arrays.asList(Arrays.copyOfRange(args, 1, args.length)), Collections.<String>emptySet(), Collections.<String, String>emptyMap(), Type.ANY);
                }
                BaseCommand child = command.getChild(args[0]);
                if (child == null)
                {
                    break;
                }
                command = child;
                labels.push(args[0]);
                args = Arrays.copyOfRange(args, 1, args.length);
            }
        }

        // TODO aliascmd prefix & suffix

        CommandContext ctx = command.getContextFactory().parse(command, sender, labels, args);
        if (command instanceof ContainerCommand && (!tabComplete || ctx.getRawIndexed().size() != 1))
        {
            DelegatingContextFilter delegation = ((ContainerCommand)command).getDelegation();
            if (delegation != null)
            {
                String child = delegation.delegateTo(ctx);
                if (child != null)
                {
                    BaseCommand target = command.getChild(child);
                    if (target != null)
                    {
                        return target.getContextFactory().parse(target, sender, labels, args);
                    }
                    // TODO command.getModule().getLog().warn("Child delegation failed: child '{}' not found!", child);
                    throw new IllegalArgumentException();
                }
            }
        }
        return ctx;
    }

    protected abstract void handleCommandException(CommandContext ctx, BaseCommandSender sender, Throwable t);
    /*
    if (!CubeEngine.isMainThread())
        {
            final Throwable tmp = t;
            sender.getCore().getTaskManager().callSync(new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    handleCommandException(context, sender, tmp);
                    return null;
                }
            });
            return;
        }
        if (t instanceof InvocationTargetException || t instanceof ExecutionException)
        {
            t = t.getCause();
        }
        if (t instanceof MissingParameterException)
        {
            sender.sendTranslated(NEGATIVE, "The parameter {name#parameter} is missing!", t.getMessage());
        }
        else if (t instanceof IncorrectUsageException)
        {
            IncorrectUsageException e = (IncorrectUsageException)t;
            if (e.getMessage() != null)
            {
                sender.sendMessage(t.getMessage());
            }
            else
            {
                sender.sendTranslated(NEGATIVE, "That seems wrong...");
            }
            if (e.getDisplayUsage())
            {
                final String usage;
                if (context != null)
                {
                    usage = context.getCommand().getUsage(context);
                }
                else
                {
                    // TODO can this happen at all?
                    usage = this.command.getUsage(sender);
                }
                sender.sendTranslated(MessageType.NEUTRAL, "Proper usage: {message}", usage);
            }
        }
        else if (t instanceof InvalidArgumentException)
        {
            InvalidArgumentException e = (InvalidArgumentException)t;
            if (e.getMessage() != null)
            {
                sender.sendMessage(t.getMessage());
            }
            else
            {
                sender.sendTranslated(NEGATIVE, "Invalid Argument...");
            }
        }
        else if (t instanceof PermissionDeniedException)
        {
            PermissionDeniedException e = (PermissionDeniedException)t;
            if (e.getMessage() != null)
            {
                sender.sendMessage(e.getMessage());
            }
            else
            {
                sender.sendTranslated(NEGATIVE, "You're not allowed to do this!");
                sender.sendTranslated(NEGATIVE, "Contact an administrator if you think this is a mistake!");
            }
            sender.sendTranslated(NEGATIVE, "Missing permission: {name}", e.getPermission());
        }
        else
        {
            sender.sendTranslated(CRITICAL, "An unknown error occurred while executing this command!");
            sender.sendTranslated(CRITICAL, "Please report this error to an administrator.");
            this.command.getModule().getLog().debug(t, t.getLocalizedMessage());
        }
     */
}
