package de.cubeisland.engine.command.parameter.reader;

import de.cubeisland.engine.command.old.exception.ReaderException;

import java.util.Locale;

public interface ArgumentReader
{
    Object read(ReaderManager manager, Class type, String arg, Locale locale) throws ReaderException;
}
