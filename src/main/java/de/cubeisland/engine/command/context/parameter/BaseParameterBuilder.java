package de.cubeisland.engine.command.context.parameter;

import de.cubeisland.engine.command.Completer;
import de.cubeisland.engine.command.context.reader.ArgumentReader;

public abstract class BaseParameterBuilder<ParamT extends BaseParameter<?>, SourceT> extends ParameterBuilder<ParamT, SourceT>
{
    protected BaseParameterBuilder(Class<ParamT> clazz)
    {
        super(clazz);
    }

    /**
     * Sets the Type and Reader Type of the Parameter
     *
     * @param type the type
     * @param reader the readerType
     */
    public void setType(Class<?> type, Class<?> reader)
    {
        this.param().type = type;
        if (reader == Void.class || reader == null)
        {
            reader = type;
        }
        if (!ArgumentReader.hasReader(reader))
        {
            throw new IllegalArgumentException("Unreadable Type for Parameter: " + reader.getName()); // TODO custom RTE
        }
        this.param().reader = reader;
    }

    /**
     * Sets how greedy the parameter is.
     *
     * A Parameter with a greed of 1 will read 1 String
     * A Parameter with a greed of 2 will read 2 Strings
     * A Parameter with a greed of -1 will read as many Strings as there are
     *
     * @param greed the greed
     */
    public void setGreed(int greed)
    {
        this.param().greed = greed;
    }

    /**
     * Sets whether the parameter is required or not.
     *
     * @param required is required
     */
    public void setRequired(boolean required)
    {
        this.param().required = required;
    }

    /**
     * Sets the description of the parameter
     *
     * @param description the description
     */
    public void setDescription(String description)
    {
        this.param().description = description;
    }

    /**
     * Sets the label for the value of the parameter
     *
     * @param valueLabel the label
     */
    public void setValueLabel(String valueLabel)
    {
        this.param().valueLabel = valueLabel;
    }

    /**
     * Adds a static label and value for the value of the parameter
     *
     * @param label the label and value
     * @param reader the readerType
     */
    public void addStaticValueLabel(String label, Class<?> reader)
    {
        if (reader == Void.class || reader == null)
        {
            reader = String.class;
        }
        this.param().staticReaders.put(label, reader);
    }

    /**
     * Sets the TabCompleter class for the parameter
     *
     * @param completer the completers class
     */
    public void setCompleter(Class<? extends Completer> completer)
    {
        try
        {
            this.setCompleter(completer.newInstance());
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    public void setCompleter(Completer<?> completer)
    {
        this.param().completer = completer;
    }
}
