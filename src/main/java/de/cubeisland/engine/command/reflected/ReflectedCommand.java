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

import de.cubeisland.engine.command.*;
import de.cubeisland.engine.command.BaseCommand;
import de.cubeisland.engine.command.exception.CommandException;

public class ReflectedCommand extends BaseCommand
{
    private final Method method;
    private final Object holder;

    public ReflectedCommand(CommandManager manager, ReflectedCommandDescriptor descriptor)
    {
        super(manager, descriptor);
        this.method = descriptor.getMethod();
        this.holder = descriptor.getAlias();
    }

    @Override
    public CommandResult run(final BaseCommandContext context)
    {
        if (method == null)
        {
            this.help(context);
            return null;
        }

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
        return null;
    }

    @Override
    public void help(BaseCommandContext ctx)
    {
        if (method == null)
        {
            this.containerHelp(ctx);
        }
        else
        {
            this.reflectedHelp(ctx);
        }

    }

    protected void reflectedHelp(BaseCommandContext ctx)
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

    protected void containerHelp(BaseCommandContext ctx)
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


}
