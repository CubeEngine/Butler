package de.cubeisland.engine.command.methodbased;

import de.cubeisland.engine.command.context.parameter.*;

public class MethodFlagFactory extends FlagFactory<FlagParameter, Flag>
{
    public MethodFlagFactory()
    {
        super(FlagParameter.class);
    }

    @Override
    public MethodFlagFactory build(Flag source)
    {
        this.begin();
        this.setName(source.name());
        this.setLongName(source.longName());
        return this;
    }
}
