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
package de.cubeisland.engine.command.parametric;

import java.util.Locale;
import java.util.UUID;
import de.cubeisland.engine.command.CommandBase;
import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.ProviderManager;
import de.cubeisland.engine.command.completer.CompleterProvider;
import de.cubeisland.engine.command.parameter.ParameterUsageGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ParametricCommandTest
{
    private ProviderManager providerManager;
    private CompleterProvider completerProvider;
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
    private TestParametricCommand container;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        providerManager = new ProviderManager();
        CompositeCommandBuilder<InvokableMethod> builder = new CompositeCommandBuilder(new ParametricBuilder(
            new ParameterUsageGenerator()));

        container = new TestParametricCommand(builder);
        container.registerSubCommands();
    }

    @Test
    public void testCmd() throws Exception
    {
        for (CommandBase command : container.getCommands())
        {
            assertTrue(command.execute(new CommandInvocation(source, command.getDescriptor().getDescription(),
                                                             providerManager)));
        }
    }
}
