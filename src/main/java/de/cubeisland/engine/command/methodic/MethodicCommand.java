package de.cubeisland.engine.command.methodic;

import java.lang.reflect.Method;
import java.util.List;

import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.DispatcherCommand;

public class MethodicCommand extends DispatcherCommand
{
    private final Object object;
    private final Method method;

    public MethodicCommand(CommandDescriptor descriptor, Object holder, Method method)
    {
        super(descriptor);
        this.object = holder;
        this.method = method;
    }

    @Override
    public boolean run(CommandCall call, List<String> parentCalls)
    {
        boolean ran = super.run(call, parentCalls);
        if (!ran)
        {
            ran = this.run(this.buildContext(call, parentCalls));
        }
        return ran;
    }

    protected boolean run(CommandContext commandContext)
    {


    }

    protected CommandContext buildContext(CommandCall call, List<String> parentCalls)
    {

    }
}
