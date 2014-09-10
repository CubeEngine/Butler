package de.cubeisland.engine.command;

import java.util.List;

public interface CommandBuilder
{
    List<CommandBase> buildCommands(Object object);
}
