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
        assertTrue(cmd.run(new CommandCall("I get matched as one String by this greedy parameter", readerManager, Locale.getDefault()),
                           Collections.<String>emptyList()));
    }

    @Command(desc = "A Simple TestCommand")
    @Params(positional = @Param(greed = INFINITE_GREED))
    public boolean runMethodic(ParameterizedContext ctx)
    {
        return true;
    }
}
