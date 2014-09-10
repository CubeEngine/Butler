package de.cubeisland.engine.command.old;

import de.cubeisland.engine.command.old.context.parameter.DescriptorBuilder;

public abstract class ExternalBaseCommandBuilder<CmdT extends BaseCommand, SourceT, DSourceT> extends BaseCommandBuilder<CmdT>
{
    private final Class<CmdT> clazz;
    private final DescriptorBuilder<?, DSourceT> descriptorFactory;


    protected ExternalBaseCommandBuilder(Class<CmdT> clazz, DescriptorBuilder<?, DSourceT> descriptorFactory)
    {
        this.clazz = clazz;
        this.descriptorFactory = descriptorFactory;
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
    public de.cubeisland.engine.command.context.CtxDescriptor newDescriptor(DSourceT source)
    {
        return descriptorFactory.build(source);
    }

    /**
     * Builds a command
     *
     * @param source the source
     */
    protected abstract ExternalBaseCommandBuilder<CmdT, SourceT, DSourceT> build(SourceT source);

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
