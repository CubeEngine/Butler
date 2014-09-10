package de.cubeisland.engine.command.parameter;

import de.cubeisland.engine.command.CommandCall;

public class NonPositionalParameter extends ParameterBase
{
    public NonPositionalParameter(String valueLabel, String description, boolean required, int greed, Class<?> type, Class<?> reader, String[] names)
    {
        super(valueLabel, description, required, greed, type, reader, names);
    }

    public NonPositionalParameter(String valueLabel, String description, boolean required, Class<?> type, Class<?> reader, String[] names)
    {
        super(valueLabel, description, required, type, reader, names);
    }

    public NonPositionalParameter(String valueLabel, String description, boolean required, int greed, Class<?> type, Class<?> reader)
    {
        super(valueLabel, description, required, greed, type, reader);
    }

    public NonPositionalParameter(String valueLabel, String description, boolean required, Class<?> type, Class<?> reader)
    {
        super(valueLabel, description, required, type, reader);
    }

    @Override
    public boolean accepts(String[] tokens, int offset)
    {
        if (this.isNamed())
        {
            for (String alias : this.getNames())
            {
                if (alias.equalsIgnoreCase(tokens[offset]))
                {
                    int remainingTokens = tokens.length - offset - 1;
                    if (remainingTokens > this.getGreed()) // TODO handle split up StringTokens
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            // TODO how to handle non named and non positional parameters
        }
        return false;
    }

    @Override
    public ParsedParameter parse(CommandCall call, String[] tokens, int beginOffset)
    {
        Object read = call.getManager().read(this.getReader(), this.getType(), tokens[beginOffset], call.getLocale());

        return ParsedParameter.of(this, read, tokens[beginOffset], 1);
    }
}
