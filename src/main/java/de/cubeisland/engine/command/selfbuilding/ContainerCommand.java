package de.cubeisland.engine.command.selfbuilding;

import de.cubeisland.engine.command.DispatcherCommand;

public class ContainerCommand extends DispatcherCommand
{
    public ContainerCommand(MutableCommandDescriptor descriptor)
    {
        super(descriptor);
        new SelfbuildingBuilder().buildCommands(this); // Reads name alias and description from Command Annotation on implementing class
    }
}
