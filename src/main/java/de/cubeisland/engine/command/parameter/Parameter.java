package de.cubeisland.engine.command.parameter;

import de.cubeisland.engine.command.CommandCall;

/**
 * A Parameter that can accept and parse tokens
 */
public interface Parameter
{
    boolean accepts(String[] tokens, int offset);

    ParsedParameter parse(CommandCall call, String[] tokens, int beginOffset);
}
