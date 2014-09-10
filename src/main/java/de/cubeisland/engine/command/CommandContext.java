package de.cubeisland.engine.command;

import java.util.List;

/**
 * A CommandContext for a CommandCall
 *
 * @param <CallT> the CommandCall Type this CommandContext uses
 */
public class CommandContext<CallT extends CommandCall>
{
    private final CallT call;
    private final List<String> parentCalls;

    public CommandContext(CallT call, List<String> parentCalls)
    {
        this.call = call;
        this.parentCalls = parentCalls;
    }

    /**
     * Returns the CommandCall
     *
     * @return the CommandCall
     */
    public CallT getCall()
    {
        return call;
    }

    /**
     * Returns the parent calls
     *
     * @return the parent calls
     */
    public List<String> getParentCalls()
    {
        return parentCalls;
    }
}
