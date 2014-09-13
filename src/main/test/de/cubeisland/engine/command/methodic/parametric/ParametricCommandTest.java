package de.cubeisland.engine.command.methodic.parametric;

import java.util.Collections;
import java.util.Locale;

import de.cubeisland.engine.command.Command;
import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.parameter.reader.ReaderManager;
import junit.framework.TestCase;

import static de.cubeisland.engine.command.parameter.property.Greed.INFINITE_GREED;

public class ParametricCommandTest extends TestCase
{
    public static final String CMD_STRING = "I get matched as one String by this greedy parameter";
    private ParametricCommand cmd;
    private ReaderManager readerManager;

    @Override
    public void setUp() throws Exception
    {
        readerManager = new ReaderManager();
        readerManager.registerDefaultReader();
        cmd = new ParametricBuilder().build(this, ParametricCommandTest.class.getMethod("runParametric",
                                                                                        CommandContext.class, String.class));
    }


    public void testCmd() throws Exception
    {
        assertTrue(cmd.run(new CommandCall(CMD_STRING, readerManager, Locale.getDefault()),
                           Collections.<String>emptyList()));
    }

    @Command(desc = "A Simple TestCommand")
    public boolean runParametric(CommandContext ctx, @Index @Greed(INFINITE_GREED) String aString)
    {
        assertEquals(aString, CMD_STRING);
        return true;
    }
}
