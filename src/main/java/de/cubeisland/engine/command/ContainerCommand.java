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

import de.cubeisland.engine.command.reflected.ReflectedCommand;

public abstract class ContainerCommand extends BaseCommand implements CommandHolder
{
    private final Class<? extends BaseCommand> subCommandType;
    private DelegatingContextFilter delegation;

    public ContainerCommand(CommandManager manager, CommandOwner owner, String name, String description)
    {
        this(manager, owner, ReflectedCommand.class, name, description);
    }

    public ContainerCommand(CommandManager manager, CommandOwner owner, Class<? extends BaseCommand> subCommandType,
                            String name, String description)
    {
        super(manager, owner, name, description, new ContextFactory().addIndexed(CommandParameterIndexed.emptyIndex(
            "action")), null);
        this.subCommandType = subCommandType;
        this.delegation = null;
    }

    public void delegateChild(final String name)
    {
        this.delegation = new DelegatingContextFilter()
        {
            @Override
            public String delegateTo(CommandContext context)
            {
                return name;
            }
        };
    }

    public void delegateChild(DelegatingContextFilter filter)
    {
        this.delegation = filter;
    }

    public Class<? extends BaseCommand> getCommandType()
    {
        return this.subCommandType;
    }

    @Override
    public CommandResult run(CommandContext context)
    {
        this.help(context);
        return null;
    }

    public DelegatingContextFilter getDelegation()
    {
        return this.delegation;
    }

    @Override
    public void help(CommandContext ctx)
    {
        ctx.sendMessage(this.getDescription());

        if (ctx.getCommand().hasChildren())
        {
            ctx.sendMessage("Usage: " + this.getUsage(ctx));
            ctx.sendMessage("Actions:");
            for (BaseCommand command : ctx.getCommand().getChildren())
            {
                ctx.sendMessage(" - " + command.getName());
            }
        }
        else
        {
            ctx.sendMessage("No Actions available!");
        }
    }

    /* // TODO proper help in CE
    BaseCommandSender sender = context.getSender();
        context.sendTranslated(NONE, "{text:Usage:color=INDIGO}: {input#usage}", this.getUsage(context));
        context.sendMessage(" ");

        List<CubeCommand> commands = new ArrayList<>();
        for (CubeCommand command : context.getCommand().getChildren())
        {
            if (command.isAuthorized(sender))
            {
                commands.add(command);
            }
        }

        if (commands.isEmpty())
        {
            context.sendTranslated(NEGATIVE, "No actions are available");
        }
        else
        {
            context.sendTranslated(NEUTRAL, "The following actions are available:");
            context.sendMessage(" ");
            for (CubeCommand command : commands)
            {
                context.sendMessage(YELLOW + command.getName() + WHITE + ": "  + GREY + sender.getTranslation(NONE, command.getDescription()));
            }
        }
        context.sendMessage(" ");
        context.sendTranslated(NONE, "{text:Detailed help:color=GREY}: {input#link:color=INDIGO}", "http://engine.cubeisland.de/c/" + this.getLabels(
            "/"));
     */

    public static abstract class DelegatingContextFilter
    {
        public abstract String delegateTo(CommandContext context);

        public CommandContext filterContext(CommandContext context, String child)
        {
            return context;
        }
    }
}
