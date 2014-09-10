package de.cubeisland.engine.command;

import de.cubeisland.engine.command.CommandDescriptor;

public class SimpleCommandDescriptor implements CommandDescriptor
{
    private String name;
    private String description;
    private String[] aliases;

    public SimpleCommandDescriptor(String name, String description, String[] aliases)
    {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String[] getAliases()
    {
        return this.aliases;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }
}
