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
package org.cubeengine.butler.parametric;

import org.cubeengine.butler.CommandBase;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.SimpleCommandDescriptor;
import org.cubeengine.butler.completer.CompleterProvider;
import org.cubeengine.butler.parameter.ParameterUsageGenerator;
import org.cubeengine.butler.parametric.builder.ParametricBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ParametricCommandTest
{
    private CompleterProvider completerProvider;
    private TestParametricCommand container;
    private SimpleCommandManager scm;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        SimpleCommandDescriptor desc = new SimpleCommandDescriptor();
        desc.setName("Base Dispatcher");
        desc.setUsageGenerator(new ParameterUsageGenerator());
        scm = new SimpleCommandManager(desc);

        scm.getProviderManager().registerBuilder(InvokableMethod.class, new ParametricBuilder(new ParameterUsageGenerator()));

        container = new TestParametricCommand(scm);
        container.registerSubCommands();
    }

    @Test
    public void testCmd() throws Exception
    {
        for (CommandBase command : container.getCommands())
        {
            try
            {
                assertTrue(command.execute(new CommandInvocation(null, command.getDescriptor().getDescription(), scm.getProviderManager())));
            }
            catch (Exception e)
            {
                System.out.println(command.getDescriptor().getName());
                throw e;
            }
        }
    }
}

