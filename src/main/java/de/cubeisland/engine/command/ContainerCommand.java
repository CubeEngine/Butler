/**
 * This file is part of CubeEngine.
 * CubeEngine is licensed under the GNU General Public License Version 3.
 *
 * CubeEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CubeEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CubeEngine.  If not, see <http://www.gnu.org/licenses/>.
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
