package de.cubeisland.engine.command.parameter;

import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.parameter.ParameterGroup;

public interface UsageGenerator
{
    String generateUsage(CommandSource source, ParameterGroup parameters);
}
