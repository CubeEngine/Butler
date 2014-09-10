package de.cubeisland.engine.command;

import java.util.List;

public class CommandContext<CallT extends CommandCall>
{
    private final CallT call;
    private final String[] rawArgs;
    private final List<String> parentCalls;

    public CommandContext(CallT call, String[] rawArgs, List<String> parentCalls)
    {
        this.call = call;
        this.rawArgs = rawArgs;
        this.parentCalls = parentCalls;
    }

    public CallT getCall()
    {
        return call;
    }

    public String[] getRawArgs()
    {
        return rawArgs;
    }

    public List<String> getParentCalls()
    {
        return parentCalls;
    }
}
