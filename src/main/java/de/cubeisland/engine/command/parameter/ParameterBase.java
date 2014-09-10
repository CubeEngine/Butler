package de.cubeisland.engine.command.parameter;

/**
 * The Base for more complex Parameters
 */
public abstract class ParameterBase implements Parameter
{
    private static final int VARIABLE_POSITION = -1;
    private final String valueLabel;
    private final String description;
    private final boolean required;
    private final int greed; // amount of tokens consumed by this parameter
    private final Class<?> type;
    private final Class<?> reader;
    
    private final String[] names;

    protected ParameterBase(String valueLabel, String description, boolean required, int greed, Class<?> type, Class<?> reader, String[] names)
    {
        this.valueLabel = valueLabel;
        this.description = description;
        this.required = required;
        this.greed = greed;
        this.type = type;
        this.reader = reader;
        this.names = names;
    }

    protected ParameterBase(String valueLabel, String description, boolean required, Class<?> type, Class<?> reader, String[] names)
    {
        this(valueLabel, description, required, 1, type, reader, names);
    }

    protected ParameterBase(String valueLabel, String description, boolean required, int greed, Class<?> type, Class<?> reader)
    {
        this(valueLabel, description, required, greed, type, reader, null);
    }

    protected ParameterBase(String valueLabel, String description, boolean required, Class<?> type, Class<?> reader)
    {
        this(valueLabel, description, required, 1, type, reader);
    }

    public boolean isNamed()
    {
        return this.names != null;
    }

    public String getValueLabel()
    {
        return valueLabel;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean isRequired()
    {
        return required;
    }

    public int getGreed()
    {
        return greed;
    }

    public Class<?> getType()
    {
        return type;
    }

    public Class<?> getReader()
    {
        return reader;
    }

    public String[] getNames()
    {
        return names;
    }

    //TODO  Static Reader ? replace them with named param with no consuming / caution when parsing we'll need to map to alias name not actual name!
    //TODO completer ?

}
