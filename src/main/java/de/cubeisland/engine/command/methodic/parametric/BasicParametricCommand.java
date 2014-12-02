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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.methodic.BasicMethodicCommand;
import de.cubeisland.engine.command.methodic.InvokableMethod;
import de.cubeisland.engine.command.methodic.InvokableMethodProperty;
import de.cubeisland.engine.command.methodic.context.BaseCommandContext;
import de.cubeisland.engine.command.methodic.context.ParameterizedContext;
import de.cubeisland.engine.command.parameter.FlagParameter;
import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.parameter.ParameterGroup;
import de.cubeisland.engine.command.parameter.ParsedParameter;
import de.cubeisland.engine.command.parameter.ParsedParameters;
import de.cubeisland.engine.command.parameter.property.MethodIndex;
import de.cubeisland.engine.command.parameter.property.group.FlagGroup;
import de.cubeisland.engine.command.parameter.property.group.NonPositionalGroup;
import de.cubeisland.engine.command.parameter.property.group.PositionalGroup;
import de.cubeisland.engine.command.result.CommandResult;

public class BasicParametricCommand extends BasicMethodicCommand
{
    public BasicParametricCommand(CommandDescriptor descriptor)
    {
        super(descriptor);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean run(BaseCommandContext commandContext)
    {
        try
        {
            InvokableMethod invokableMethod = this.getDescriptor().valueFor(InvokableMethodProperty.class);
            Object[] args = new Object[invokableMethod.getMethod().getParameterTypes().length];
            args[0] = commandContext;

            ParameterGroup params = this.getDescriptor().valueFor(ParameterGroup.class);
            List<Parameter> parameters = new ArrayList<>(params.valueFor(FlagGroup.class));
            parameters.addAll(params.valueFor(NonPositionalGroup.class));
            parameters.addAll(params.valueFor(PositionalGroup.class));
            Collections.sort(parameters, MethodIndex.COMPARATOR);

            List<ParsedParameter> parsedParams = new ArrayList<>(commandContext.getInvocation().valueFor(ParsedParameters.class));
            for (Parameter parameter : parameters)
            {
                Integer index = parameter.valueFor(MethodIndex.class);
                ParsedParameter match = null;
                for (ParsedParameter parsedParam : parsedParams)
                {
                    if (parsedParam.getParameter() == parameter)
                    {
                        match = parsedParam;
                    }
                }
                if (match != null)
                {
                    parsedParams.remove(match);
                    args[index] = match.getParsedValue();
                }
                else
                {
                    if (parameter instanceof FlagParameter)
                    {
                        args[index] = false;
                    }
                    else
                    {
                        args[index] = null;
                    }
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
            else if (result instanceof CommandResult)
            {
                ((CommandResult)result).process(commandContext);
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            this.handleException(e, commandContext.getInvocation());
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
