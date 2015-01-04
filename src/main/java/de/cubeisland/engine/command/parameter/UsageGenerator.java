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
package de.cubeisland.engine.command.parameter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.Dispatcher;
import de.cubeisland.engine.command.DispatcherProperty;
import de.cubeisland.engine.command.StringUtils;

/**
 * Provides the ability to generate a usage for a {@link ParameterGroup}
 */
public abstract class UsageGenerator
{
    /**
     * Returns the names of the given command and all its dispatchers
     *
     * @param descriptor the command
     *
     * @return the names
     */
    public static Stack<String> getNames(CommandDescriptor descriptor)
    {
        Stack<String> cmds = new Stack<>();
        cmds.push(descriptor.getName());
        Dispatcher dispatcher = descriptor.valueFor(DispatcherProperty.class).getDispatcher();
        while (dispatcher != null && dispatcher.getDescriptor() != null)
        {
            descriptor = dispatcher.getDescriptor();
            if (descriptor.getName().isEmpty())
            {
                break;
            }
            cmds.add(descriptor.getName());
            dispatcher = descriptor.valueFor(DispatcherProperty.class).getDispatcher();
        }
        return cmds;
    }

    /**
     * Generates the usage for given {@link ParameterGroup}
     *
     * @param invocation     the invocation
     * @param parameters the {@link de.cubeisland.engine.command.parameter.ParameterGroup}
     *
     * @return the generated usage string
     */
    protected abstract String generateParameterUsage(CommandInvocation invocation, ParameterGroup parameters);

    /**
     * Generates the usage for a given CommandDescriptor and CommandSource
     *
     * @param invocation the invocation
     * @param descriptor the {@link de.cubeisland.engine.command.CommandDescriptor}
     *
     * @return the complete usage String
     */
    public final String generateUsage(CommandInvocation invocation, CommandDescriptor descriptor, String... plabels)
    {
        List<String> labels;
        if (plabels != null && plabels.length > 0)
        {
            labels = Arrays.asList(plabels);
        }
        else if (invocation == null)
        {
            labels = getNames(descriptor);
            Collections.reverse(labels);
        }
        else
        {
            labels = invocation.getLabels();
        }
        return getPrefix(invocation) + StringUtils.join(" ", labels) + " " +
            this.generateParameterUsage(invocation, descriptor.valueFor(ParameterGroup.class)).trim();
    }

    /**
     * Returns a prefix to attach for given CommandSource
     *
     * @param invocation the invocation
     *
     * @return the prefix
     */
    protected String getPrefix(CommandInvocation invocation)
    {
        return "";
    }
}
