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
package org.cubeengine.butler.parameter;

import org.cubeengine.butler.CommandDescriptor;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.StringUtils;
import org.cubeengine.butler.parameter.parser.FixedValueParser;
import org.cubeengine.butler.parameter.parser.FlagParser;
import org.cubeengine.butler.parameter.parser.GroupParser;
import org.cubeengine.butler.parameter.parser.NamedParser;
import org.cubeengine.butler.parameter.property.Properties;
import org.cubeengine.butler.parameter.property.Requirement;
import org.cubeengine.butler.parametric.ParametricCommandDescriptor;

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

        Parameter parameters = ((ParametricCommandDescriptor)descriptor).getParameters();
        if (parameters.getParser() instanceof GroupParser)
        {
            sb.append(generateGroupUsage(invocation, ((GroupParser)parameters.getParser())));
        }
        else // Not a group? Print it alone.
        {
            sb.append(generateParameterUsage(invocation, parameters));
        }
        return sb.toString();
    }

    /**
     * Generates the usage for given {@link FlagParser}
     *
     * @param invocation   the invocation
     * @param parameter
     * @return the usage string
     */
    protected String generateFlagUsage(CommandInvocation invocation, Parameter parameter)
    {
        return "[-" + parameter.getProperty(Properties.FLAG_LONGNAME) + "]";
    }

    /**
     * Generates the usage for given {@link Parameter}
     *
     * @param invocation the {@link CommandInvocation}
     * @param parameter the {@link org.cubeengine.butler.parameter.Parameter}
     *
     * @return the usage string
     */
    protected String generateParameterUsage(CommandInvocation invocation, Parameter parameter)
    {
        if (parameter.getParser() instanceof GroupParser)
        {
            if (Requirement.isRequired(parameter))
            {
                return "<" + generateGroupUsage(invocation, ((GroupParser)parameter.getParser())) + ">";
            }
            return "[" + generateGroupUsage(invocation, ((GroupParser)parameter.getParser())) + "]";
        }
        String valueLabel = parameter.getProperty(Properties.VALUE_LABEL);
        if (valueLabel != null)
        {
            valueLabel = this.valueLabel(invocation, valueLabel);
        }
        else if (parameter.getParser() instanceof FixedValueParser)
        {
            valueLabel = StringUtils.join("|", ((FixedValueParser)parameter.getParser()).getFixedValues());
        }
        else
        {
            valueLabel = "param";
        }

        if (parameter.getParser() instanceof NamedParser)
        {
            valueLabel = ((NamedParser)parameter.getParser()).getNames()[0] + " <" + valueLabel + ">";
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

    private String generateGroupUsage(CommandInvocation invocation, GroupParser parser)
    {
        StringBuilder sb = new StringBuilder();
        for (Parameter parameter : parser.getPositional())
        {
            sb.append(generateParameterUsage(invocation, parameter)).append(" ");
        }

        for (Parameter parameter : parser.getNonPositional())
        {
            sb.append(generateParameterUsage(invocation, parameter)).append(" ");
        }

        for (Parameter parameter : parser.getFlags())
        {
            if (parameter.getParser() instanceof FlagParser)
            {
                sb.append(generateFlagUsage(invocation, parameter)).append(" ");
            }
            else
            {
                throw new IllegalArgumentException("Expected FlagParameter but found " + parameter.getClass().getName());
            }
        }
        return sb.toString();
    }

    /**
     * Possibly manipulates the label and returns it.
     * <p>This can be overwritten to for example translate the label</p>
     *
     * @param invocation     the {@link  CommandInvocation}
     * @param valueLabel the valueLabel
     *
     * @return the manipulated label
     */
    protected String valueLabel(CommandInvocation invocation, String valueLabel)
    {
        return valueLabel;
    }
}
