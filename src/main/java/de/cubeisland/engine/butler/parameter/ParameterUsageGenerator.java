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
package de.cubeisland.engine.butler.parameter;

import de.cubeisland.engine.butler.CommandDescriptor;
import de.cubeisland.engine.butler.CommandInvocation;
import de.cubeisland.engine.butler.StringUtils;
import de.cubeisland.engine.butler.parameter.property.Requirement;
import de.cubeisland.engine.butler.parameter.property.ValueLabel;
import de.cubeisland.engine.butler.parametric.ParametricCommandDescriptor;

/**
 * A simple Implementation of the UsageGenerator
 */
public class ParameterUsageGenerator extends UsageGenerator
{
    @Override
    public String generateParameterUsage(CommandInvocation invocation, CommandDescriptor descriptor)
    {
        StringBuilder sb = new StringBuilder();

        if (!(descriptor instanceof ParametricCommandDescriptor))
        {
            return "<parameters>";
        }

        ParameterGroup parameters = ((ParametricCommandDescriptor)descriptor).getParameters();
        for (Parameter parameter : parameters.getPositional())
        {
            sb.append(generateParameterUsage(invocation, parameter)).append(" ");
        }

        for (Parameter parameter : parameters.getNonPositional())
        {
            sb.append(generateParameterUsage(invocation, parameter)).append(" ");
        }

        for (Parameter parameter : parameters.getFlags())
        {
            if (parameter instanceof FlagParameter)
            {
                sb.append(generateFlagUsage(invocation, (FlagParameter)parameter)).append(" ");
            }
            else
            {
                throw new IllegalArgumentException("Expected FlagParameter but found " + parameter.getClass().getName());
            }
        }

        return sb.toString();
    }

    /**
     * Generates the usage for given {@link FlagParameter}
     *
     * @param invocation   the invocation
     * @param parameter the {@link de.cubeisland.engine.butler.parameter.Parameter}
     *
     * @return the usage string
     */
    protected String generateFlagUsage(CommandInvocation invocation, FlagParameter parameter)
    {
        return "[-" + parameter.longName() + "]";
    }

    /**
     * Generates the usage for given {@link Parameter}
     *
     * @param invocation    the {@link de.cubeisland.engine.butler.CommandSource}
     * @param parameter the {@link de.cubeisland.engine.butler.parameter.Parameter}
     *
     * @return the usage string
     */
    protected String generateParameterUsage(CommandInvocation invocation, Parameter parameter)
    {
        if (parameter instanceof ParameterGroup)
        {
            if (Requirement.isRequired(parameter))
            {
                return "<" + this.generateParameterUsage(invocation, (ParameterGroup)parameter) + ">";
            }
            return "[" + this.generateParameterUsage(invocation, (ParameterGroup)parameter) + "]";
        }
        String valueLabel = parameter.valueFor(ValueLabel.class);
        if (valueLabel != null)
        {
            valueLabel = this.valueLabel(invocation, valueLabel);
        }
        else if (parameter instanceof FixedValueParameter)
        {
            valueLabel = StringUtils.join("|", ((FixedValueParameter)parameter).getFixedValues());
        }
        else
        {
            valueLabel = "param";
        }

        if (parameter instanceof NamedParameter)
        {
            valueLabel = ((NamedParameter)parameter).getNames()[0] + " <" + valueLabel + ">";
        }

        if (Requirement.isRequired(parameter))
        {
            return "<" + valueLabel + ">";
        }
        else
        {
            return "[" + valueLabel + "]";
        }
    }

    /**
     * Possibly manipulates the label and returns it.
     * <p>This can be overwritten to for example translate the label</p>
     *
     * @param invocation     the {@link de.cubeisland.engine.butler.CommandSource}
     * @param valueLabel the valueLabel
     *
     * @return the manipulated label
     */
    protected String valueLabel(CommandInvocation invocation, String valueLabel)
    {
        return valueLabel;
    }
}
