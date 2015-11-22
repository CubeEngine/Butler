/**
 * The MIT License
 * Copyright (c) 2014 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cubeengine.butler.parametric;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.DispatcherCommand;
import org.cubeengine.butler.parameter.FlagParameter;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parameter.ParameterGroup;
import org.cubeengine.butler.parameter.ParsedParameter;
import org.cubeengine.butler.parameter.ParsedParameters;
import org.cubeengine.butler.parameter.property.MethodIndex;
import org.cubeengine.butler.result.CommandResult;

public class BasicParametricCommand extends DispatcherCommand
{
    public BasicParametricCommand(ParametricCommandDescriptor descriptor)
    {
        super(descriptor);
    }

    @Override
    public boolean selfExecute(CommandInvocation invocation)
    {
        boolean ran = super.selfExecute(invocation);
        if (!ran)
        {
            invocation.setProperty(new ParsedParameters());
            this.getDescriptor().getParameters().parse(invocation);
            ran = this.run(invocation);
        }
        return ran;
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        List<String> suggestions = new ArrayList<>();
        invocation.setProperty(new ParsedParameters());
        invocation.setProperty(new SuggestionParameters(new ArrayList<Parameter>()));

        List<String> superSuggestions = super.getSuggestions(invocation); // do this first before token is consumed!
        suggestions.addAll(this.getDescriptor().getParameters().getSuggestions(invocation));

        if (superSuggestions != null)
        {
            suggestions.addAll(superSuggestions);
        }
        return suggestions;
    }

    @SuppressWarnings("unchecked")
    protected boolean run(CommandInvocation invocation)
    {
        try
        {
            ParametricCommandDescriptor descriptor = this.getDescriptor();
            InvokableMethod invokableMethod = descriptor.getInvokableMethod();
            Class<?>[] parameterTypes = invokableMethod.getMethod().getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i <= descriptor.getContextParameter(); i++)
            {
                args[i] = invocation.getContext(parameterTypes[i]);
            }


            ParameterGroup params = descriptor.getParameters();
            List<Parameter> parameters = new ArrayList<>(params.getFlags());
            parameters.addAll(params.getNonPositional());
            parameters.addAll(params.getPositional());
            Collections.sort(parameters, MethodIndex.COMPARATOR);

            List<ParsedParameter> parsedParams = new ArrayList<>(invocation.valueFor(ParsedParameters.class));
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
                ((CommandResult)result).process(invocation); // TODO recreate context for CommandResult OR somehow ensure the types are the same
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            this.handleException(e, invocation);
        }
        return false;
    }

    @Override
    public ParametricCommandDescriptor getDescriptor()
    {
        return (ParametricCommandDescriptor)super.getDescriptor();
    }
}
