package de.cubeisland.engine.command.old.base.method;

import de.cubeisland.engine.command.old.context.parameter.*;

public class AnnotatedFlagBuilder extends FlagBuilder<FlagParameter, Flag>
{
    public AnnotatedFlagBuilder()
    {
        super(FlagParameter.class);
    }

    @Override
    public AnnotatedFlagBuilder build(Flag source)
    {
        this.begin();
        this.setName(source.name());
        this.setLongName(source.longName());
        return this;
    }
}
