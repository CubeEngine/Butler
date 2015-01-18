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
package de.cubeisland.engine.command.parametric;

import java.util.ArrayList;
import java.util.List;
import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.SimpleCommandDescriptor;
import de.cubeisland.engine.command.filter.Filter;
import de.cubeisland.engine.command.filter.FilterException;
import de.cubeisland.engine.command.parameter.ParameterGroup;
import de.cubeisland.engine.command.parametric.context.ContextBuilder;

public class ParametricCommandDescriptor extends SimpleCommandDescriptor

    implements ParametricDescriptor, Filter
{
    private InvokableMethod invokableMethod;
    private int contextParameter;
    private List<Filter> filters = new ArrayList<>();
    private ParameterGroup parameters;

    public InvokableMethod getInvokableMethod()
    {
        return invokableMethod;
    }

    public void setInvokableMethod(InvokableMethod invokableMethod)
    {
        this.invokableMethod = invokableMethod;
    }

    public int getContextParameter()
    {
        return contextParameter;
    }

    public void setContextParameter(int contextParameter)
    {
        this.contextParameter = contextParameter;
    }

    public void addFilter(Filter filter)
    {
        this.filters.add(filter);
    }

    @Override
    public void run(CommandInvocation invocation) throws FilterException
    {
        for (Filter filter : this.filters)
        {
            filter.run(invocation);
        }
    }

    public ParameterGroup getParameters()
    {
        return parameters;
    }

    public void setParameters(ParameterGroup parameters)
    {
        this.parameters = parameters;
    }

    protected Object getContext(CommandInvocation invocation, Class<?> parameterType)
    {
        if (getDispatcher().getBaseDispatcher().getDescriptor() instanceof ContextBuilder)
        {
            return ((ContextBuilder)getDispatcher().getBaseDispatcher().getDescriptor()).buildContext(invocation, parameterType);
        }
        throw new IllegalArgumentException("Missing ContextBuilder");
    }
}
