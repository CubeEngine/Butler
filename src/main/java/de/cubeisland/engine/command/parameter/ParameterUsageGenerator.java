package de.cubeisland.engine.command.parameter;

import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.StringUtils;
import de.cubeisland.engine.command.parameter.property.FixedValues;
import de.cubeisland.engine.command.parameter.property.Required;
import de.cubeisland.engine.command.parameter.property.ValueLabel;
import de.cubeisland.engine.command.parameter.property.group.FlagGroup;
import de.cubeisland.engine.command.parameter.property.group.NonPositionalGroup;
import de.cubeisland.engine.command.parameter.property.group.PositionalGroup;

public class ParameterUsageGenerator implements UsageGenerator
{
    @Override
    public String generateUsage(CommandSource source, ParameterGroup parameters)
    {
        StringBuilder sb = new StringBuilder();

        for (Parameter parameter : parameters.valueFor(PositionalGroup.class))
        {
            sb.append(generateParameterUsage(source, parameter));
        }

        for (Parameter parameter : parameters.valueFor(NonPositionalGroup.class))
        {
            sb.append(generateParameterUsage(source, parameter));
        }

        for (Parameter parameter : parameters.valueFor(FlagGroup.class))
        {
            if (parameter instanceof FlagParameter)
            {
                sb.append(generateFlagUsage(source, (FlagParameter)parameter));
            }
            else
            {
                throw new IllegalArgumentException("Expected FlagParameter but found " + parameter.getClass().getName());
            }
        }

        return sb.toString();
    }

    protected String generateFlagUsage(CommandSource source, FlagParameter parameter)
    {
        return "[-" + parameter.longName() + "]";
    }

    protected String generateParameterUsage(CommandSource source, Parameter parameter)
    {
        String valueLabel = parameter.valueFor(ValueLabel.class);
        String[] fixedValues = parameter.valueFor(FixedValues.class);
        if (valueLabel != null)
        {
            if (fixedValues != null)
            {
                valueLabel = fixedValues[0] + " <" + valueLabel + ">";
            }
        }
        else if (fixedValues != null)
        {
            valueLabel = StringUtils.join("|", fixedValues);
            if (!parameter.valueFor(Required.class))
            {
                return "[" + valueLabel + "]";
            }
            return valueLabel;
        }
        else
        {
            valueLabel = "param";
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
}
