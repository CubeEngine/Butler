package de.cubeisland.engine.command.selfbuilding;

import de.cubeisland.engine.command.CommandDescriptor;

/**
 * A CommandDescriptor that can be changed after creation
 */
public interface MutableCommandDescriptor extends CommandDescriptor
{
    /**
     * Sets the commands name
     *
     * @param name the name
     */
    void setName(String name);

    /**
     * Sets the commands aliases
     *
     * @param aliases the aliases
     */
    void setAliases(String[] aliases);

    /**
     * Sets the commands description
     *
     * @param description the description
     */
    void setDescription(String description);
}
