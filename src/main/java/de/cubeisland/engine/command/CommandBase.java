package de.cubeisland.engine.command;

import java.util.List;

/**
 * A command that can be run
 */
public interface CommandBase extends CommandCompleter
{
    /**
     * Handles the CommandCall
     *
     * @param call the CommandCall
     * @param parentCalls the parent commands
     * @return true if the command was executed succesfully
     */
    boolean run(CommandCall call, List<String> parentCalls);

    /**
     * Returns the Descriptor of this command
     *
     * @return the CommandDescriptor
     */
    CommandDescriptor getDescriptor();
}
