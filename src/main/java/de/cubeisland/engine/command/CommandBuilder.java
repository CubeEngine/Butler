package de.cubeisland.engine.command;

import java.util.List;

/**
 * Provides a Method to build commands from an Object
 */
public interface CommandBuilder
{
    /**
     * Returns a list of commands built from the object
     *
     * @param object the object to build commands from
     * @return a list of created commandsd
     */
    List<CommandBase> buildCommands(Object object);
}
