package de.cubeisland.engine.command;

public interface CommandDescriptor
{
    String getName();
    String[] getAliases();
    String getDescription();
}
