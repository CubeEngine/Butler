package de.cubeisland.engine.command.parameter;

public class PositionalParameter extends ParameterBase
{
    private final int position;

    public PositionalParameter(String valueLabel, String description, boolean required, int greed, Class<?> type, Class<?> reader, String[] names, int position)
    {
        super(valueLabel, description, required, greed, type, reader, names);
        this.position = position;
    }

    public PositionalParameter(String valueLabel, String description, boolean required, Class<?> type, Class<?> reader, String[] names, int position)
    {
        super(valueLabel, description, required, type, reader, names);
        this.position = position;
    }

    public PositionalParameter(String valueLabel, String description, boolean required, int greed, Class<?> type, Class<?> reader, int position)
    {
        super(valueLabel, description, required, greed, type, reader);
        this.position = position;
    }

    public PositionalParameter(String valueLabel, String description, boolean required, Class<?> type, Class<?> reader, int position)
    {
        super(valueLabel, description, required, type, reader);
        this.position = position;
    }

    @Override
    public boolean accepts(String[] tokens, int offset)
    {
        return false;
    }

    @Override
    public ParsedParameter parse(CallT call, String[] tokens, int beginOffset)
    {
        return null;
    }
}
