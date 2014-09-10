package de.cubeisland.engine.command.parameter;

import java.util.List;

public class ParsedParameter<ParsedT>
{
    private final Parameter parameter;
    private final ParsedT parsedValue;
    private final int consumed;

    private ParsedParameter(Parameter parameter, ParsedT parsedString, int consumed)
    {
        this.parameter = parameter;
        this.parsedValue = parsedString;
        this.consumed = consumed;
    }

    public Parameter getParameter()
    {
        return parameter;
    }

    public ParsedT getParsedValue()
    {
        return parsedValue;
    }

    public static ParsedParameter<String> empty()
    {
        return new ParsedParameter<>(null, "", 1);
    }

    public static ParsedParameter<String> of(Parameter parameter, String parsed, int consumed)
    {
        return new ParsedParameter<>(parameter, parsed, consumed);
    }

    public static ParsedParameter<List<ParsedParameter>> of (Parameter parameter, List<ParsedParameter> parsed)
    {
        int consumed = 0;
        for (ParsedParameter parsedParameter : parsed)
        {
            consumed += parsedParameter.consumed;
        }
        return new ParsedParameter<>(parameter, parsed, consumed);
    }

    public int getConsumed()
    {
        return consumed;
    }
}
