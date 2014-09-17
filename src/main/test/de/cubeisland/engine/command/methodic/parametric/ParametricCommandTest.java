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
    private ParametricCommand cmd1;
    private ParametricCommand cmd2;
    private ReaderManager readerManager;

    @Override
    public void setUp() throws Exception
    {
        readerManager = new ReaderManager();
        readerManager.registerDefaultReader();
        cmd1 = new ParametricBuilder().build(this, ParametricCommandTest.class.getMethod("cmd1", CommandContext.class, String.class));
        cmd2 = new ParametricBuilder().build(this, ParametricCommandTest.class.getMethod("cmd2", CommandContext.class, TestEnum.class, TestEnum.class));
    }


    public void testCmd() throws Exception
    {
        assertTrue(cmd1.run(new CommandCall(CMD_STRING, readerManager, Locale.getDefault()), Collections.<String>emptyList()));
        assertTrue(cmd2.run(new CommandCall("Value1 Value2", readerManager, Locale.getDefault()), Collections.<String>emptyList()));
    }

    @Command(desc = "A Simple TestCommand")
    public boolean cmd1(CommandContext ctx, @Index @Greed(INFINITE_GREED) String aString)
    {
        assertEquals(aString, CMD_STRING);
        return true;
    }

    @Command(desc = "A Simple TestCommand")
    public boolean cmd2(CommandContext ctx, @Index TestEnum aEnum, @Index TestEnum aEnum2)
    {
        assertEquals(aEnum, TestEnum.VALUE1);
        assertEquals(aEnum2, TestEnum.VALUE2);
        return true;
    }

    enum TestEnum
    {
        VALUE1, VALUE2
    }
}
