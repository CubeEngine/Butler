/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anselm Brehme, Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cubeisland.engine.command.methodic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.methodic.context.BaseCommandContext;
import de.cubeisland.engine.command.methodic.context.ParameterizedContext;
import de.cubeisland.engine.command.methodic.parametric.Greed;
import de.cubeisland.engine.command.methodic.parametric.Index;
import de.cubeisland.engine.command.methodic.parametric.ParametricBuilder;
import de.cubeisland.engine.command.parameter.reader.ReaderManager;
import de.cubeisland.engine.command.tokenized.TokenizedInvocation;
import junit.framework.TestCase;

import static de.cubeisland.engine.command.parameter.property.Greed.INFINITE_GREED;

public class MethodicCommandTest extends TestCase
{
    private ReaderManager readerManager;

    private List<BasicMethodicCommand> commands;
    private List<String> commandLines = new ArrayList<>();

    private CompositeCommandBuilder builder;

    private CommandSource source = new CommandSource()
    {
        @Override
        public String getName()
        {
            return null;
        }

        @Override
        public UUID getUUID()
        {
            return null;
        }

        @Override
        public Locale getLocale()
        {
            return null;
        }
    };

    @Override
    public void setUp() throws Exception
    {
        readerManager = new ReaderManager();
        readerManager.registerDefaultReader();
        builder = new CompositeCommandBuilder(new MethodicBuilder(), new ParametricBuilder());
        this.commands = builder.buildCommands(this);
        this.commandLines.add("I get matched as one String by this greedy parameter");
        this.commandLines.add("First Second Second too");

        this.commandLines.add("Also a long String that gets matched completely");
        this.commandLines.add("Value1 Value2");
    }


    public void testCmd() throws Exception
    {
        Iterator<String> iterator = commandLines.iterator();
        for (BasicMethodicCommand command : commands)
        {
            assertTrue(command.run(new TokenizedInvocation(source, iterator.next(), readerManager)));
        }
    }

    @Command(desc = "TestCommand 1")
    @Params(positional = @Param(greed = INFINITE_GREED))
    public boolean methodic1(ParameterizedContext ctx)
    {
        assertEquals(ctx.getStrings(0), ctx.getCall().getCommandLine());
        return true;
    }

    @Command(desc = "TestCommand 2")
    @Params(positional = {@Param(),
                          @Param(greed = INFINITE_GREED)})
    public boolean methodic2(ParameterizedContext ctx)
    {
        assertEquals(ctx.getString(0), "First");
        assertEquals(ctx.getStrings(1), "Second Second too");
        return true;
    }


    @Command(desc = "A Simple TestCommand")
    public boolean parametric1(BaseCommandContext ctx, @Index @Greed(INFINITE_GREED) String aString)
    {
        assertEquals(aString, ctx.getCall().getCommandLine());
        return true;
    }

    @Command(desc = "A Simple TestCommand")
    public boolean parametric2(BaseCommandContext ctx, @Index TestEnum aEnum, @Index TestEnum aEnum2)
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
