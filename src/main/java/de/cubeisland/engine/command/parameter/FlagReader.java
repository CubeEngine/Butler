package de.cubeisland.engine.command.parameter;

import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.old.ReaderException;
import de.cubeisland.engine.command.parameter.reader.ArgumentReader;
import de.cubeisland.engine.command.parameter.reader.ReaderManager;

/**
 * An ArgumentReader for Flags
 */
public class FlagReader implements ArgumentReader
{
    private final String name;
    private final String longName;

    public FlagReader(String name, String longName)
    {
        this.name = name;
        this.longName = longName;
    }

    @Override
    public Object read(ReaderManager manager, Class type, CommandCall call) throws ReaderException
    {
        String flag = call.currentToken();
        if (flag.startsWith("-"))
        {
            flag = flag.substring(1);
            if (this.name.equalsIgnoreCase(flag) || this.longName.equalsIgnoreCase(flag))
            {
                call.consume(1);
                return true;
            }
        }
        return null;
    }
}
