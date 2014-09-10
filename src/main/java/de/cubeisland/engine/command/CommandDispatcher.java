package de.cubeisland.engine.command;

import java.util.Set;

/**
 * Handles command dispatching
 */
public interface CommandDispatcher extends CommandBase
{
    /**
     * Registers a command to this dispatcher
     *
     * @param command
     */
    void registerCommand(CommandBase command);
    /*
    get alias from cmd / also get replacement policy
     */

    /**
     * Returns the commands registered for this dispatcher
     *
     * @return the registered commands
     */
    Set<CommandBase> getCommands();

    /**
     * Returns whether this dispatcher has a command with given alias
     *
     * @param alias the alias
     * @return true if a command is registered for given alias
     */
    boolean contains(String alias);

    /**
     * Returns a registered command for given alias
     *
     * @param alias the alias
     * @return the command for given alias
     */
    CommandBase get(String alias);
}
