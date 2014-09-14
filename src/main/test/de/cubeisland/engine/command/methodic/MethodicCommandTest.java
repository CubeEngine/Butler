package de.cubeisland.engine.command.methodic;

import java.util.Collections;
import java.util.Locale;

import de.cubeisland.engine.command.Command;
import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.parameter.ParameterizedContext;
import de.cubeisland.engine.command.parameter.reader.ReaderManager;
import junit.framework.TestCase;

import static de.cubeisland.engine.command.parameter.property.Greed.INFINITE_GREED;

public class MethodicCommandTest extends TestCase
{
    public static final String COMMAND_TEXT = "I get matched as one String by this greedy parameter";
    private MethodicCommand cmd;
    private ReaderManager readerManager;

    @Override
    public void setUp() throws Exception
    {
        readerManager = new ReaderManager();
        readerManager.registerDefaultReader();
        cmd = new MethodicBuilder().build(this, MethodicCommandTest.class.getMethod("runMethodic",
                                                                                    ParameterizedContext.class));
    }


    public void testCmd() throws Exception
    {
        assertTrue(cmd.run(new CommandCall(COMMAND_TEXT, readerManager, Locale.getDefault()),
                           Collections.<String>emptyList()));
    }

    @Command(desc = "A Simple TestCommand")
    @Params(positional = @Param(greed = INFINITE_GREED))
    public boolean runMethodic(ParameterizedContext ctx)
    {
        assertEquals(ctx.getStrings(0), COMMAND_TEXT);
        return true;
    }
}
