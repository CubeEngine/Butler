package de.cubeisland.engine.command.context.parameter;

public abstract class ParameterBuilder<ParamT, SourceT>
{
    private ParamT parameter;
    private Class<ParamT> clazz;

    protected ParameterBuilder(Class<ParamT> clazz)
    {
        this.clazz = clazz;
    }

    /**
     * Builds a Parameter
     *
     * @param source the source
     *
     * @return fluent interface
     */
    public abstract ParameterBuilder<ParamT, SourceT> build(SourceT source);

    /**
     * Starts Building a new Parameter
     */
    public final void begin()
    {
        try
        {
            this.parameter = clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns the parameter currently building
     *
     * @return the current parameter
     */
    protected final ParamT param()
    {
        return this.parameter;
    }


    /**
     * Returns the finalized Parameter.
     * This Factory will, when used after this cause {@link java.lang.NullPointerException}s as the underlying parameter is no longer available.
     *
     * @return the parameter
     */
    public final ParamT finish()
    {
        ParamT parameter = this.parameter;
        this.parameter = null; // prevent modification later
        return parameter;
    }
}
