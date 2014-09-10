package de.cubeisland.engine.command;

/**
 * An Object describing a command
 */
public interface CommandDescriptor
{
    /**
     * Returns the name of the command
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the aliases of the command
     *
     * @return the aliases
     */
    String[] getAliases();

    /**
     * Returns the description of the command
     *
     * @return the description
     */
    String getDescription();
}
