/*
 * The MIT License
 * Copyright © 2014 Cube Island
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
package org.cubeengine.butler.parameter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.cubeengine.butler.CommandBase;
import org.cubeengine.butler.SimpleCommandDescriptor;
import org.cubeengine.butler.parametric.BasicParametricCommand;
import org.cubeengine.butler.parametric.Command;
import org.cubeengine.butler.parametric.InvokableMethod;
import org.cubeengine.butler.parametric.Label;
import org.cubeengine.butler.parametric.Optional;
import org.cubeengine.butler.parametric.SimpleCommandManager;
import org.cubeengine.butler.parametric.builder.ParametricBuilder;
import org.cubeengine.butler.parametric.context.BasicCommandContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParameterUsageGeneratorTest
{
    private List<CommandBase> commands = new ArrayList<>();
    private SimpleCommandManager scm;

    @Before
    public void setUp() throws Exception
    {
        SimpleCommandDescriptor desc = new SimpleCommandDescriptor();
        desc.setName("Base Dispatcher");
        desc.setUsageGenerator(new ParameterUsageGenerator());
        scm = new SimpleCommandManager(desc);
        ParametricBuilder builder = new ParametricBuilder(new ParameterUsageGenerator());
        for (Method method : ParametricBuilder.getMethods(this.getClass()))
        {
            CommandBase cmd = builder.buildCommand(scm, new InvokableMethod(method, this));
            if (cmd != null)
            {
                commands.add(cmd);
            }
        }
    }

    @Test
    public void testGenerator() throws Exception
    {
        for (CommandBase command : commands)
        {
            assertEquals(command.getDescriptor().getDescription(), command.getDescriptor().getUsage(null));
        }
    }

    @Command(desc = "cmd1 <aString>")
    public void cmd1(BasicCommandContext ctx, @Label("aString") String aString)
    {
    }

    @Command(desc = "cmd2 [aString]")
    public void cmd2(BasicCommandContext ctx, @Label("aString") @Optional String aString)
    {
    }
}
