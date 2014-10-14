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
package de.cubeisland.engine.command.methodic;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.methodic.context.BaseCommandContext;
import de.cubeisland.engine.command.methodic.context.ParameterizedContext;
import de.cubeisland.engine.command.parameter.ParameterGroup;
import de.cubeisland.engine.command.parameter.ParsedParameters;
import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.DispatcherCommand;

public class BasicMethodicCommand extends DispatcherCommand
{
    public BasicMethodicCommand(CommandDescriptor descriptor)
    {
        super(descriptor);
    }

    @Override
    public boolean run(CommandInvocation invocation)
    {
        boolean ran = super.run(invocation);
        if (!ran)
        {
            invocation.setProperty(new ParsedParameters());
            this.getDescriptor().valueFor(ParameterGroup.class).parseParameter(invocation);
            ran = this.run(this.buildContext(invocation));
        }
        return ran;
    }

    @Override
    public List<String> getSuggestions(CommandInvocation call)
    {
        // TODO parse Parameters til last parameter then tabcomplete
        return super.getSuggestions(call);
        // TODO completer stuff
    }

    /**
     * Runs this command with given CommandContext
     *
     * @param commandContext the CommandContext
     * @return whether the command was executed succesfully
     */
    protected boolean run(BaseCommandContext commandContext)
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
        try
        {
            Object result = this.getDescriptor().valueFor(InvokableMethodProperty.class).invoke(commandContext);
            if (result == null)
            {
                return true;
            }
            else if (result instanceof Boolean)
            {
                return (Boolean)result;
            }
            else
            {
                // TODO CommandResult
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalArgumentException(e); // TODO
        }
        return false;
    }

    protected BaseCommandContext buildContext(CommandInvocation call)
    {
        // TODO check with method which context is allowed
        return new ParameterizedContext(call);
    }
}
