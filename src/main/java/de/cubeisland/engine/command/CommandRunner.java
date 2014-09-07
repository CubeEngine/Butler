package de.cubeisland.engine.command;

import de.cubeisland.engine.command.context.CommandContext;
import de.cubeisland.engine.command.result.CommandResult;

public interface CommandRunner<CtxT extends CommandContext>
{
    /**
     * This method handles the command execution
     *
     * @param context The CommandContext containing all the necessary information
     *
     * @return the CommantResult
     */
    CommandResult run(CtxT context);
}
