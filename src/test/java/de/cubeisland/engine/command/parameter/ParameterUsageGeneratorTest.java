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
package de.cubeisland.engine.command.parameter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.methodic.BasicMethodicCommand;
import de.cubeisland.engine.command.methodic.Command;
import de.cubeisland.engine.command.methodic.InvokableMethod;
import de.cubeisland.engine.command.methodic.InvokableMethodProperty;
import de.cubeisland.engine.command.methodic.MethodicBuilder;
import de.cubeisland.engine.command.methodic.context.BaseCommandContext;
import de.cubeisland.engine.command.methodic.parametric.Index;
import de.cubeisland.engine.command.methodic.parametric.Label;
import de.cubeisland.engine.command.methodic.parametric.Optional;
import de.cubeisland.engine.command.methodic.parametric.ParametricBuilder;
import junit.framework.TestCase;
import org.junit.Test;

public class ParameterUsageGeneratorTest extends TestCase
{
    private List<BasicMethodicCommand> commands = new ArrayList<>();

    @Override
    public void setUp() throws Exception
    {
        ParametricBuilder<InvokableMethod> builder = new ParametricBuilder<>();
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
    public void testGenerator() throws Exception
    {
        for (BasicMethodicCommand command : commands)
        {
            assertEquals(command.getDescriptor().getDescription(), command.getDescriptor().getUsage(null));
        }
    }

    @Command(desc = "<aString>")
    public void cmd1(BaseCommandContext ctx, @Index @Label("aString") String aString)
    {}

    @Command(desc = "[aString]")
    public void cmd2(BaseCommandContext ctx, @Index @Label("aString") @Optional String aString)
    {}
}
