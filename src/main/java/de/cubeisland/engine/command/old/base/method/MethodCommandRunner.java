package de.cubeisland.engine.command.old.base.method;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.cubeisland.engine.command.CommandRunnerOld;
import de.cubeisland.engine.command.old.context.CommandContext;
import de.cubeisland.engine.command.old.exception.CommandException;
import de.cubeisland.engine.command.old.result.CommandResult;

public class MethodCommandRunner<CtxT extends CommandContext> implements CommandRunnerOld<CtxT>
{
    private final Object holder;
    final Method method;


    public MethodCommandRunner(Object holder, Method method)
    {
        this.holder = holder;
        this.method = method;
        this.method.setAccessible(true);
    }

    @Override
    public CommandResult run(CtxT context)
    {
        try
        {
            Object result = this.method.invoke(this.holder, context);
            if (result instanceof CommandResult)
            {
                return (CommandResult)result;
            }
            return null;
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            if (e.getCause() instanceof CommandException)
            {
                throw (CommandException)e.getCause();
            }
            throw new RuntimeException(e.getCause());
        }
    }
}
