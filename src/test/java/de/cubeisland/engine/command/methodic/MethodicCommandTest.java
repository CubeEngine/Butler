/**
 * The MIT License
 * Copyright (c) 2014 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.cubeisland.engine.command.methodic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.methodic.context.BasicCommandContext;
import de.cubeisland.engine.command.methodic.parametric.Command;
import de.cubeisland.engine.command.methodic.parametric.Greed;
import de.cubeisland.engine.command.methodic.parametric.ParametricBuilder;
import de.cubeisland.engine.command.parameter.reader.ReaderManager;
import org.junit.Before;
import org.junit.Test;

import static de.cubeisland.engine.command.parameter.Parameter.INFINITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MethodicCommandTest
{
    private ReaderManager readerManager;
    private List<BasicMethodicCommand> commands = new ArrayList<>();
    private CommandSource source = new CommandSource()
    {
        @Override
        public String getName()
        {
            return null;
        }

        @Override
        public UUID getUniqueId()
        {
            return null;
        }

        @Override
        public Locale getLocale()
        {
            return null;
        }
    };

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        readerManager = new ReaderManager();
        readerManager.registerDefaultReader();
        CompositeCommandBuilder<InvokableMethod> builder = new CompositeCommandBuilder(new ParametricBuilder());
        for (Method method : MethodicBuilder.getMethods(this.getClass()))
        {
            BasicMethodicCommand cmd = builder.buildCommand(new InvokableMethodProperty(method, this));
            if (cmd != null)
            {
                commands.add(cmd);
            }
        }

    }

    @Test
    public void testCmd() throws Exception
    {
        for (BasicMethodicCommand command : commands)
        {
            assertTrue(command.execute(new CommandInvocation(source, command.getDescriptor().getDescription(), readerManager)));
        }
    }

    @Command(desc = "Also a long String that gets matched completely")
    public boolean parametric1(BasicCommandContext ctx, @Greed(INFINITE) String aString)
    {
        assertEquals(aString, ctx.getInvocation().getCommandLine());
        return true;
    }

    @Command(desc = "Value1 Value2")
    public boolean parametric2(BasicCommandContext ctx, TestEnum aEnum, TestEnum aEnum2)
    {
        assertEquals(aEnum, TestEnum.VALUE1);
        assertEquals(aEnum2, TestEnum.VALUE2);
        return true;
    }

    enum TestEnum
    {
        VALUE1,
        VALUE2
    }
}
