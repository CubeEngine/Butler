package de.cubeisland.engine.command.context.parameter;

public abstract class FlagFactory<FlagT extends FlagParameter, SourceT> extends ParameterFactory<FlagT, SourceT>
{
    public FlagFactory(Class<FlagT> clazz)
    {
        super(clazz);
    }

    public void setName(String name)
    {
        this.param().name = name;
    }

    public void setLongName(String longName)
    {
        this.param().longName = longName;
    }
}
