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
package org.cubeengine.butler.parameter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.cubeengine.butler.CommandDescriptor;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.Dispatcher;

import static org.cubeengine.butler.StringUtils.join;

/**
 * Provides the ability to generate a usage for a {@link ParameterGroup}
 */
public abstract class UsageGenerator
{
    /**
     * Generates the usage for a given CommandDescriptor and CommandSource
     *
     * @param invocation the invocation
     * @param descriptor the {@link CommandDescriptor}
     * @param labels the labels
     *
     * @return the complete usage String
     */
    public final String generateUsage(CommandInvocation invocation, CommandDescriptor descriptor, String... labels)
    {
        List<String> labelList;
        if (labels != null && labels.length > 0)
        {
            labelList = Arrays.asList(labels);
        }
        else if (invocation == null)
        {
            labelList = getNames(descriptor);
            Collections.reverse(labelList);
        }
        else
        {
            labelList = invocation.getLabels();
        }
        return getPrefix(invocation) + join(" ", labelList) + " " + this.generateParameterUsage(invocation, descriptor).trim();
    }

    /**
     * Returns the names of the given command and all its dispatchers
     *
     * @param descriptor the command
     *
     * @return the names
     */
    public static Stack<String> getNames(CommandDescriptor descriptor)
    {
        Stack<String> commands = new Stack<>();
        commands.push(descriptor.getName());
        Dispatcher dispatcher = descriptor.getDispatcher();
        while (dispatcher != null && dispatcher.getDescriptor() != null)
        {
            descriptor = dispatcher.getDescriptor();
            if (descriptor.getName().isEmpty())
            {
                break;
            }
            commands.add(descriptor.getName());
            dispatcher = descriptor.getDispatcher();
        }
        return commands;
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

    /**
     * Generates the usage for given {@link ParameterGroup}
     *
     * @param invocation the invocation
     * @param descriptor the {@link CommandDescriptor}
     *
     * @return the generated usage string
     */
    protected abstract String generateParameterUsage(CommandInvocation invocation, CommandDescriptor descriptor);
}
