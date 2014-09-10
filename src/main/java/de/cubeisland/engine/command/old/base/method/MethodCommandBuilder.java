package de.cubeisland.engine.command.old.base.method;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import de.cubeisland.engine.command.CommandBase;
import de.cubeisland.engine.command.old.ExternalBaseCommandBuilder;
import de.cubeisland.engine.command.annotations.Command;
import de.cubeisland.engine.command.old.context.ContextFactory;
import de.cubeisland.engine.command.old.context.CtxDescriptor;

public abstract class MethodCommandBuilder<CmdT extends CommandBase> extends ExternalBaseCommandBuilder<CmdT, MethodCommandRunner, Method>
{
    protected MethodCommandBuilder(Class<CmdT> clazz, AnnotatedDescriptorBuilder<Method> descriptorFactory)
    {
        super(clazz, descriptorFactory);
    }

    public MethodCommandBuilder(Class<CmdT> clazz)
    {
        this(clazz, new AnnotatedDescriptorBuilder<Method>());
    }

    protected Method method(MethodCommandRunner runner)
    {
        return runner.method;
    }

    @Override
    protected MethodCommandBuilder<CmdT> build(MethodCommandRunner runner)
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
