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
    public static final String COMMAND_TEXT_1 = "I get matched as one String by this greedy parameter";
    private MethodicCommand cmd1;
    private MethodicCommand cmd2;
    private ReaderManager readerManager;

    @Override
    public void setUp() throws Exception
    {
        readerManager = new ReaderManager();
        readerManager.registerDefaultReader();
        cmd1 = new MethodicBuilder().build(this, MethodicCommandTest.class.getMethod("cmd1", ParameterizedContext.class));
        cmd2 = new MethodicBuilder().build(this, MethodicCommandTest.class.getMethod("cmd2", ParameterizedContext.class));
    }


    public void testCmd() throws Exception
    {
        assertTrue(cmd1.run(new CommandCall(COMMAND_TEXT_1, readerManager, Locale.getDefault()), Collections.<String>emptyList()));
        assertTrue(cmd2.run(new CommandCall("First Second Second too", readerManager, Locale.getDefault()), Collections.<String>emptyList()));
    }

    @Command(desc = "TestCommand 1")
    @Params(positional = @Param(greed = INFINITE_GREED))
    public boolean cmd1(ParameterizedContext ctx)
    {
        assertEquals(ctx.getStrings(0), COMMAND_TEXT_1);
        return true;
    }

    @Command(desc = "TestCommand 2")
    @Params(positional = {@Param(),
                          @Param(greed = INFINITE_GREED)})
    public boolean cmd2(ParameterizedContext ctx)
    {
        assertEquals(ctx.getString(0), "First");
        assertEquals(ctx.getStrings(1), "Second Second too");
        return true;
    }
}
