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
package de.cubeisland.engine.command.methodic.parametric;

import java.lang.reflect.InvocationTargetException;

import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.methodic.BasicMethodicCommand;
import de.cubeisland.engine.command.methodic.InvokableMethod;
import de.cubeisland.engine.command.methodic.InvokableMethodProperty;
import de.cubeisland.engine.command.methodic.context.BaseCommandContext;
import de.cubeisland.engine.command.parameter.ParsedParameter;
import de.cubeisland.engine.command.parameter.ParsedParameters;
import de.cubeisland.engine.command.parameter.property.MethodIndex;
import de.cubeisland.engine.command.CommandInvocation;

public class BasicParametricCommand extends BasicMethodicCommand
{
    public BasicParametricCommand(CommandDescriptor descriptor)
    {
        super(descriptor);
    }

    @Override
    protected boolean run(BaseCommandContext commandContext)
    {
        try
        {
            InvokableMethod invokableMethod = this.getDescriptor().valueFor(InvokableMethodProperty.class);
            Object[] args = new Object[invokableMethod.getMethod().getParameterTypes().length];
            args[0] = commandContext;
            for (ParsedParameter parameter : commandContext.getInvocation().valueFor(ParsedParameters.class))
            {
                Integer methodIndex = parameter.getParameter().valueFor(MethodIndex.class);
                if (methodIndex != null)
                {
                    args[methodIndex] = parameter.getParsedValue();
                }
            }

            Object result = invokableMethod.invoke(args);
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

    @Override
    protected BaseCommandContext buildContext(CommandInvocation invocation)
    {
        // TODO if method needs ParameterizedContext give it
        return new BaseCommandContext(invocation);
    }
}
