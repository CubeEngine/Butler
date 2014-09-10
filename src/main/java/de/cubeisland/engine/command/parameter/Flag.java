package de.cubeisland.engine.command.parameter;

public class Flag implements Parameter
{
    private final String name;
    private final String longName;

    public Flag(String name, String longName)
    {
        this.name = name;
        this.longName = longName;
    }

    @Override
    public boolean accepts(String[] tokens, int offset)
    {
        String token = tokens[offset];
        if (token.startsWith("-"))
        {
            token = token.substring(1);
            return this.name.equalsIgnoreCase(token) || this.longName.equalsIgnoreCase(token);
        }
        return false;
    }

    @Override
    public ParsedParameter parse(CallT call, String[] tokens, int beginOffset)
    {
        return ParsedParameter.of(this, tokens[beginOffset], 1);
    }
}
