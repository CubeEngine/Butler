package de.cubeisland.engine.command;

import de.cubeisland.engine.command.parameter.UsageGenerator;
import de.cubeisland.engine.command.property.AbstractProperty;

public class UsageProvider extends AbstractProperty<UsageGenerator>
{
    public UsageProvider(UsageGenerator value)
    {
        super(value);
    }
}
