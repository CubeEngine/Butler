package de.cubeisland.engine.command.parameter;

import java.util.LinkedList;

public interface Parameter
{
    boolean accepts(String[] tokens, int offset);

    ParsedParameter parse(String[] tokens, int offset);


}
