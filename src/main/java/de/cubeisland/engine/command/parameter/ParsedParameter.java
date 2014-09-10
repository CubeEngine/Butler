package de.cubeisland.engine.command.parameter;

import java.util.List;

/**
 * A ParsedParameter
 *
 * @param <ParsedT> the Type of the parsed Value
 */
public class ParsedParameter<ParsedT>
{
    private final Parameter parameter;
    private final ParsedT parsedValue;
    private final String rawValue;
    private final int consumed;

    private ParsedParameter(Parameter parameter, ParsedT parsedString, String rawValue, int consumed)
    {
        this.parameter = parameter;
        this.parsedValue = parsedString;
        this.consumed = consumed;
        this.rawValue = rawValue;
    }

    /**
     * Creates an empty ParsedParameter
     *
     * @return an empty ParsedParameter
     */
    public static ParsedParameter<String> empty()
    {
        return new ParsedParameter<>(null, "", "", 1);
    }

    /**
     * Creates a ParsedParameter
     *
     * @param parameter the parameter
     * @param parsed    the parsed value
     * @param consumed  the amount of tokens consumed
     * @return the ParsedParameter
     */
    public static <T> ParsedParameter<T> of(Parameter parameter, T parsed, String string ,int consumed)
    {
        return new ParsedParameter<>(parameter, parsed, string, consumed);
    }

    /**
     * Creates a ParsedParameter
     *
     * @param parameter the parameter
     * @param parsed    the parsed value
     * @return the ParsedParameter
     */
    public static ParsedParameter<List<ParsedParameter>> of(Parameter parameter, List<ParsedParameter> parsed)
    {
        int consumed = 0;
        for (ParsedParameter parsedParameter : parsed)
        {
            consumed += parsedParameter.consumed;
        }
        return new ParsedParameter<>(parameter, parsed, null, consumed);
    }

    /**
     * Returns the parameter
     *
     * @return the parameter
     */
    public Parameter getParameter()
    {
        return parameter;
    }

    /**
     * Returns the parsed value
     *
     * @return the parsed value
     */
    public ParsedT getParsedValue()
    {
        return parsedValue;
    }

    /**
     * Returns the amount of tokens consumed by the parameter
     *
     * @return the amount of tokens consumed
     */
    public int getConsumed()
    {
        return consumed;
    }

    /**
     * Returns the raw String or null if this is a group of Parameters
     *
     * @return the raw String
     */
    public String getRawValue()
    {
        return rawValue;
    }
}
