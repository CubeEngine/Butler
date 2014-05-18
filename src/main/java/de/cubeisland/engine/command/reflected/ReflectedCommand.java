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
package de.cubeisland.engine.command.reflected;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.cubeisland.engine.command.BaseCommand;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.CommandManager;
import de.cubeisland.engine.command.CommandOwner;
import de.cubeisland.engine.command.CommandPermission;
import de.cubeisland.engine.command.CommandResult;
import de.cubeisland.engine.command.ContextFactory;
import de.cubeisland.engine.old.command.exception.CommandException;

public class ReflectedCommand extends BaseCommand
{
    private final Object holder;
    private final Method method;
    private final Class<? extends CommandContext> contextType;

    @SuppressWarnings("unchecked")
    public ReflectedCommand(CommandManager manager, CommandOwner owner, Object holder, Method method, String name,
                            String description, ContextFactory factory, CommandPermission permission)
    {
        super(manager, owner, name, description, factory, permission);
        this.holder = holder;
        this.method = method;
        this.method.setAccessible(true);
        this.contextType = (Class<? extends CommandContext>)method.getParameterTypes()[0];
    }

    @Override
    public CommandResult run(final CommandContext context)
    {
        if (this.contextType.isInstance(context))
        {
            try
            {
                Object result = this.method.invoke(this.holder, context);
                if (result instanceof CommandResult)
                {
                    return (CommandResult)result;
                }
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e)
            {
                if (e.getCause() instanceof CommandException)
                {
                    throw (CommandException)e.getCause();
                }
                throw new RuntimeException(e.getCause());
            }
        }
        return null;
    }

    @Override
    public void help(CommandContext ctx)
    {
        ctx.sendMessage(this.getDescription());
        ctx.sendMessage("Usage: " + this.getUsage(ctx));
        if (ctx.getCommand().hasChildren())
        {
            ctx.sendMessage("SubCmds:");
            for (BaseCommand command : ctx.getCommand().getChildren())
            {
                ctx.sendMessage(" - " + command.getName());
            }
        }
    }


    /* // TODO in CE implement pretty help
    public void help(HelpContext context)
    {
        context.sendTranslated(NONE, "{text:Description:color=GREY}: {input}", this.getDescription());
        context.sendTranslated(NONE, "{text:Usage:color=GREY}: {input}", this.getUsage(context));

        if (this.hasChildren())
        {
            context.sendMessage(" ");
            context.sendTranslated(NEUTRAL, "The following subcommands are available:");
            context.sendMessage(" ");

            final BaseCommandSender sender = context.getSender();
            for (CubeCommand command : context.getCommand().getChildren())
            {
                if (command.isAuthorized(sender))
                {
                    context.sendMessage(YELLOW + command.getName() + WHITE + ": " + GREY + sender.getTranslation(NONE, command.getDescription()));
                }
            }
        }
        context.sendMessage(" ");
        context.sendTranslated(NONE, "{text:Detailed help:color=GREY}: {input#link:color=INDIGO}", "http://engine.cubeisland.de/c/" + this.getOwner().getId() + "/" + this.getLabels(
            "/"));
    }
     */
}
