package de.cubeisland.engine.command;

import java.util.Set;

/**
 * Handles command dispatching
 */
public interface CommandDispatcher extends CommandBase
{
    void registerCommand(CommandBase command);
    /*
    get alias from cmd / also get replacement policy
     */

    Set<CommandBase> getCommands();

    boolean contains(String alias);

    CommandBase get(String alias);
}
