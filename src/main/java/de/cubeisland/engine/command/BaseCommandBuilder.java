package de.cubeisland.engine.command;

import java.util.Set;

import de.cubeisland.engine.command.context.ContextFactory;
import de.cubeisland.engine.command.context.CtxDescriptor;
import de.cubeisland.engine.command.context.parameter.DescriptorFactory;

public abstract class BaseCommandBuilder<CmdT extends BaseCommand, SourceT, DSourceT>
{
    private final Class<CmdT> clazz;
    private final DescriptorFactory<?, DSourceT> descriptorFactory;
    private CmdT cmd;

    protected BaseCommandBuilder(Class<CmdT> clazz, DescriptorFactory<?, DSourceT> descriptorFactory)
    {
        this.clazz = clazz;
        this.descriptorFactory = descriptorFactory;
    }

    protected CmdT cmd()
    {
        return cmd;
    }

    /**
     * Starts Building a new Command
     */
    public final void begin()
    {
        try
        {
            this.cmd = clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Builds a Context Descriptor
     *
     * @param source the source
     *
     * @return the descriptor
     */
    public CtxDescriptor newDescriptor(DSourceT source)
    {
        return descriptorFactory.build(source);
    }

    /**
     * Initializes the command with the needed information
     *  @param name        the name
     * @param description the description
     * @param ctxFactory  the context factory
     * @param runner      the command runner
     */
    protected void init(String name, String description, ContextFactory ctxFactory, CommandRunner runner)
    {
        cmd.name = name;
        cmd.description = description;
        cmd.contextFactory = ctxFactory;
        cmd.runner = runner;
    }

    /**
     * Sets the aliases of the command
     *
     * @param aliases the aliases to set
     */
    public void setAlias(Set<String> aliases)
    {
        cmd.aliases.clear();
        cmd.aliases.addAll(aliases);
    }

    /**
     * Builds a command
     *
     * @param source the source
     */
    protected abstract BaseCommandBuilder<CmdT, SourceT, DSourceT> build(SourceT source);

    /**
     * Returns the finalized command.
     * This Builder will, when used after this cause {@link java.lang.NullPointerException}s as the underlying command is no longer available.
     *
     * @return the comand
     */
    public final CmdT finish()
    {
        CmdT cmd = this.cmd;
        this.cmd = null; // prevent modification later
        return cmd;
    }
}
