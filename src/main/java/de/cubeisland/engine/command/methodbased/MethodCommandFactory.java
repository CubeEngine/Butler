package de.cubeisland.engine.command.methodbased;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import de.cubeisland.engine.command.BaseCommand;
import de.cubeisland.engine.command.BaseCommandBuilder;
import de.cubeisland.engine.command.context.ContextFactory;
import de.cubeisland.engine.command.context.CtxDescriptor;

public abstract class MethodCommandFactory<CmdT extends BaseCommand> extends BaseCommandBuilder<CmdT, MethodCommandRunner, Method>
{

    protected MethodCommandFactory(Class<CmdT> clazz, MethodDescriptorFactory descriptorFactory)
    {
        super(clazz, descriptorFactory);
    }

    public MethodCommandFactory(Class<CmdT> clazz)
    {
        this(clazz, MethodDescriptorFactory.newInstance());
    }

    protected Method method(MethodCommandRunner runner)
    {
        return runner.method;
    }

    @Override
    protected MethodCommandFactory<CmdT> build(MethodCommandRunner runner)
    {
        this.begin();

        Command cmdAnnot = runner.method.getAnnotation(Command.class);
        String name = runner.method.getName();
        if (!cmdAnnot.name().isEmpty())
        {
            name = cmdAnnot.name();
        }
        name = name.trim().toLowerCase();

        this.init(name, cmdAnnot.desc(), newCtxFactory(this.newDescriptor(runner.method)), runner);

        Set<String> aliases = new HashSet<>();
        for (String alias : cmdAnnot.alias())
        {
            aliases.add(alias.trim().toLowerCase());
        }
        this.setAlias(aliases);
        return this;
    }

    protected abstract ContextFactory newCtxFactory(CtxDescriptor descriptor);
}
