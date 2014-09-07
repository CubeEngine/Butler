package de.cubeisland.engine.command.context.parameter;

public abstract class NamedParameterFactory<ParamT extends NamedParameter, SourceT> extends BaseParameterFactory<ParamT, SourceT>
{
    protected NamedParameterFactory(Class<ParamT> clazz)
    {
        super(clazz);
    }

    /**
     * Sets the name of the named parameter
     *
     * @param name the name
     */
    public void setName(String name)
    {
        this.param().name = name;
    }

    /**
     * Adds an alias that can be used as alternative name.
     *
     * @param name the alias
     */
    public void addAlias(String name)
    {
        this.param().aliases.add(name);
    }
}
