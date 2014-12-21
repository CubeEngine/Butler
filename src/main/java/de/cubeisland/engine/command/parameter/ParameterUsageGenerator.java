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
package de.cubeisland.engine.command.parameter;

import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.StringUtils;
import de.cubeisland.engine.command.parameter.property.Required;
import de.cubeisland.engine.command.parameter.property.ValueLabel;

/**
 * A simple Implementation of the UsageGenerator
 */
public class ParameterUsageGenerator extends UsageGenerator
{
    @Override
    public String generateUsage(CommandSource source, ParameterGroup parameters)
    {
        StringBuilder sb = new StringBuilder();


        for (Parameter parameter : parameters.getPositional())
        {
            sb.append(generateParameterUsage(source, parameter)).append(" ");
        }

        for (Parameter parameter : parameters.getNonPositional())
        {
            sb.append(generateParameterUsage(source, parameter)).append(" ");
        }

        for (Parameter parameter : parameters.getFlags())
        {
            if (parameter instanceof FlagParameter)
            {
                sb.append(generateFlagUsage(source, (FlagParameter)parameter)).append(" ");
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
     * @param source    the {@link CommandSource}
     * @param parameter the {@link Parameter}
     *
     * @return the usage string
     */
    protected String generateFlagUsage(CommandSource source, FlagParameter parameter)
    {
        // TODO check required is false! IllegalParameterException
        return "[-" + parameter.longName() + "]";
    }

    /**
     * Generates the usage for given {@link Parameter}
     *
     * @param source    the {@link CommandSource}
     * @param parameter the {@link Parameter}
     *
     * @return the usage string
     */
    protected String generateParameterUsage(CommandSource source, Parameter parameter)
    {
        if (parameter instanceof ParameterGroup)
        {
            if (parameter.valueFor(Required.class))
            {
                return "<" + this.generateUsage(source, (ParameterGroup)parameter) + ">";
            }
            return "[" + this.generateUsage(source, (ParameterGroup)parameter) + "]";
        }
        String valueLabel = parameter.valueFor(ValueLabel.class);
        if (valueLabel != null)
        {
            valueLabel = this.valueLabel(source, valueLabel);
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

        if (parameter.valueFor(Required.class))
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
     * @param source     the {@link CommandSource}
     * @param valueLabel the valueLabel
     *
     * @return the manipulated label
     */
    protected String valueLabel(CommandSource source, String valueLabel)
    {
        return valueLabel;
    }
}
