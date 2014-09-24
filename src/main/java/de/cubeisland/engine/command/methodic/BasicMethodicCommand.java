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
import de.cubeisland.engine.command.Parameters;
import de.cubeisland.engine.command.methodic.context.BaseCommandContext;
import de.cubeisland.engine.command.methodic.context.ParameterizedContext;
import de.cubeisland.engine.command.parameter.ParsedParameters;
import de.cubeisland.engine.command.tokenized.DispatcherCommand;
import de.cubeisland.engine.command.tokenized.TokenizedInvocation;

public class BasicMethodicCommand extends DispatcherCommand
{
    public BasicMethodicCommand(CommandDescriptor descriptor)
    {
        super(descriptor);
    }

    @Override
    public boolean run(TokenizedInvocation call)
    {
        boolean ran = super.run(call);
        if (!ran)
        {
            call.setProperty(new ParsedParameters());
            this.getDescriptor().valueFor(Parameters.class).parseParameter(call);
            ran = this.run(this.buildContext(call));
        }
        return ran;
    }

    @Override
    public List<String> getSuggestions(TokenizedInvocation call)
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

    protected BaseCommandContext buildContext(TokenizedInvocation call)
    {
        // TODO check with method which context is allowed
        return new ParameterizedContext(call);
    }
}
