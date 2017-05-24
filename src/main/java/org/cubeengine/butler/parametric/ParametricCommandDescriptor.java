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

import java.util.ArrayList;
import java.util.List;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.SimpleCommandDescriptor;
import org.cubeengine.butler.filter.Filter;
import org.cubeengine.butler.filter.FilterException;
import org.cubeengine.butler.parameter.GroupParser;
import org.cubeengine.butler.parameter.Parameter;

public class ParametricCommandDescriptor extends SimpleCommandDescriptor implements ParametricDescriptor, Filter
{
    private InvokableMethod invokableMethod;
    private int contextParameter;
    private List<Filter> filters = new ArrayList<>();
    private Parameter parameters;

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

    public Parameter getParameters()
    {
        return parameters;
    }

    public void setParameters(Parameter parameters)
    {
        this.parameters = parameters;
    }
}
