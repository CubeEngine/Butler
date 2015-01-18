package de.cubeisland.engine.command.methodic;

import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.parameter.reader.ReaderManager;

public interface InvocationBuilder
{
    CommandInvocation build(CommandSource source, String commandLine, String delim, ReaderManager manager);
}
