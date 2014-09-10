package de.cubeisland.engine.command.old.context.parameter;

public abstract class FlagBuilder<FlagT extends FlagParameter, SourceT> extends ParameterBuilder<FlagT, SourceT>
{
    public FlagBuilder(Class<FlagT> clazz)
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
