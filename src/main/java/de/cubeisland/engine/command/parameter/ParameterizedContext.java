package de.cubeisland.engine.command.parameter;

import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.ContextDescriptor;

public class ParameterizedContext<CallT extends CommandCall> extends CommandContext<CallT>
{
    public ParameterizedContext(CallT call, String[] tokens, List<String> parentCalls, ContextDescriptor descriptor)
    {
        super(call, parentCalls);

        ParsedParameter parsed = descriptor.parse(call, , 0);
        // TODO read parameters / flatten structure
    }


}
