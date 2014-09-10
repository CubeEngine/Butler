package de.cubeisland.engine.command.selfbuilding;

import de.cubeisland.engine.command.CommandDescriptor;

public interface MutableCommandDescriptor extends CommandDescriptor
{
    void setName(String name);
    void setAliases(String[] aliases);
    void setDescription(String description);
}
