package de.cubeisland.engine.command.methodic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.annotations.Command;
import de.cubeisland.engine.command.CommandBase;
import de.cubeisland.engine.command.CommandBuilder;
import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.SimpleCommandDescriptor;

public class MethodicBuilder implements CommandBuilder
{
    @Override
    public List<CommandBase> buildCommands(Object object)
    {
        List<CommandBase> list = new ArrayList<>();
        for (Method method : object.getClass().getMethods())
        {
            if (method.isAnnotationPresent(Command.class))
            {
                list.add(this.build(object, method));
            }
        }
        return list;
    }

    protected CommandBase build(Object holder, Method method)
    {
        return new MethodicCommand(buildDescriptor(holder, method), holder, method);
    }

    protected CommandDescriptor buildDescriptor(Object holder, Method method)
    {
        Command cmdAnnot = method.getAnnotation(Command.class);
        String name = cmdAnnot.name().isEmpty() ? method.getName() : cmdAnnot.name();
        return new SimpleCommandDescriptor(name, cmdAnnot.desc(), cmdAnnot.alias());
    }
}
