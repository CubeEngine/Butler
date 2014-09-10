package de.cubeisland.engine.command.old;

import java.util.Set;

import de.cubeisland.engine.command.old.context.ContextFactory;

public class BaseCommandBuilder<CmdT extends BaseCommand>
{
    CmdT cmd;
    protected CmdT cmd()
    {
        return cmd;
    }

    protected BaseCommandBuilder()
    {
    }

    protected BaseCommandBuilder(CmdT cmd)
    {
        this.cmd = cmd;
    }

    /**
     * Sets the aliases of the command
     *
     * @param aliases the aliases to set
     */
    protected void setAlias(Set<String> aliases)
    {
        cmd.aliases.clear();
        cmd.aliases.addAll(aliases);
    }

    /**
     * Initializes the command with the needed information
     *  @param name        the name
     * @param description the description
     * @param ctxFactory  the context factory
     * @param runner      the command runner
     */
    protected void init(String name, String description, ContextFactory ctxFactory, CommandRunnerOld runner)
    {
        cmd.name = name;
        cmd.description = description;
        cmd.contextFactory = ctxFactory;
        cmd.runner = runner;
    }
}
