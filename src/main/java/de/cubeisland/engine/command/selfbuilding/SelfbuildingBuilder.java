package de.cubeisland.engine.command.selfbuilding;

import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.annotations.Command;
import de.cubeisland.engine.command.CommandBase;
import de.cubeisland.engine.command.CommandBuilder;
import de.cubeisland.engine.command.CommandDescriptor;

/**
 * A CommandBuilder to be called from inside of a command
 * The command information are extracted from the annotation on the objects class
 */
public class SelfbuildingBuilder implements CommandBuilder
{
    @Override
    public List<CommandBase> buildCommands(Object object)
    {
        if (object instanceof CommandBase)
        {
            CommandDescriptor descriptor = ((CommandBase)object).getDescriptor();
            if (descriptor instanceof MutableCommandDescriptor)
            {
                Command command = object.getClass().getAnnotation(Command.class);
                ((MutableCommandDescriptor)descriptor).setName(command.name());
                ((MutableCommandDescriptor)descriptor).setDescription(command.desc());
                ((MutableCommandDescriptor)descriptor).setAliases(command.alias());
            }

            if (object instanceof ContainerCommand)
            {
                // TODO search for methodic Commands and register them on ContainerCommand
            }

            ArrayList<CommandBase> result = new ArrayList<>();
            result.add((CommandBase)object);
            return result;
        }
        throw new IllegalArgumentException("SelbuildingBuilder only accepts Commands implementing CommandBase");
    }
}
