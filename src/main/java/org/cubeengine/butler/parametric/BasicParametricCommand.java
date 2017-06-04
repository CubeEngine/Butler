/*
 * The MIT License
 * Copyright © 2014 Cube Island
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
import org.cubeengine.butler.DiagnoseListener.Phase;
import org.cubeengine.butler.DiagnoseListener.Stage;
import org.cubeengine.butler.DispatcherCommand;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parameter.parser.GroupParser;
import org.cubeengine.butler.parameter.parser.ParameterParser.ParameterType;
import org.cubeengine.butler.parameter.ParsedParameter;
import org.cubeengine.butler.parameter.ParsedParameters;
import org.cubeengine.butler.parameter.property.MethodIndex;
import org.cubeengine.butler.parameter.property.Properties;
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
            this.parse(invocation);
            ran = this.run(invocation);
        }
        return ran;
    }

    private void parse(CommandInvocation invocation)
    {
        invocation.getListener().on(Stage.PARSE, Phase.PRE, invocation);
        ParsedParameters parsed = new ParsedParameters();
        invocation.setProperty(parsed);
        this.getDescriptor().getParameters().parse(invocation, parsed.value(), null);
        invocation.getListener().on(Stage.PARSE, Phase.POST, invocation);
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        List<String> suggestions = new ArrayList<>();

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
            Object[] args = getArguments(invocation, descriptor, invokableMethod);

            invocation.getListener().on(Stage.INVOKE, Phase.PRE, invocation);
            Object result = invokableMethod.invoke(args);
            invocation.getListener().on(Stage.INVOKE, Phase.POST, invocation);
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
                invocation.getListener().on(Stage.PROCESS_RESULT, Phase.PRE, invocation);
                ((CommandResult)result).process(invocation); // TODO recreate context for CommandResult OR somehow ensure the types are the same
                invocation.getListener().on(Stage.PROCESS_RESULT, Phase.POST, invocation);
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            this.handleException(e, invocation);
        }
        return false;
    }

    private static Object[] getArguments(CommandInvocation invocation, ParametricCommandDescriptor descriptor, InvokableMethod invokableMethod)
    {
        invocation.getListener().on(Stage.PREPARE_ARGUMENTS, Phase.PRE, invocation);

        Class<?>[] parameterTypes = invokableMethod.getMethod().getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i <= descriptor.getContextParameter(); i++)
        {
            args[i] = invocation.getContext(parameterTypes[i]);
        }

        Parameter params = descriptor.getParameters();
        List<Parameter> parameters = new ArrayList<>();
        if (params.getParser() instanceof GroupParser)
        {
            parameters.addAll(((GroupParser)params.getParser()).getFlags());
            parameters.addAll(((GroupParser)params.getParser()).getNonPositional());
            parameters.addAll(((GroupParser)params.getParser()).getPositional());
        }
        else
        {
            parameters.add(params);
        }
        Collections.sort(parameters, MethodIndex.COMPARATOR);

        List<ParsedParameter> parsedParams = new ArrayList<>(invocation.valueFor(ParsedParameters.class));
        for (Parameter parameter : parameters)
        {
            Integer index = parameter.getProperty(Properties.METHOD_INDEX);
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
                if (parameter.getParameterType() == ParameterType.FLAG)
                {
                    args[index] = false;
                }
                else
                {
                    args[index] = null;
                }
            }
        }

        invocation.getListener().on(Stage.PREPARE_ARGUMENTS, Phase.POST, invocation);
        return args;
    }

    @Override
    public ParametricCommandDescriptor getDescriptor()
    {
        return (ParametricCommandDescriptor)super.getDescriptor();
    }
}
