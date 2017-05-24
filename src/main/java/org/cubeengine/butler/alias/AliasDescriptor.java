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
package org.cubeengine.butler.alias;

import java.util.Collections;
import java.util.List;
import org.cubeengine.butler.CommandDescriptor;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.Dispatchable;
import org.cubeengine.butler.Dispatcher;

public class AliasDescriptor implements CommandDescriptor, Dispatchable
{
    private String name;
    private CommandDescriptor descriptor;
    private Dispatcher dispatcher;

    public AliasDescriptor(String name, CommandDescriptor descriptor)
    {
        this.name = name;
        this.descriptor = descriptor;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public List<AliasConfiguration> getAliases()
    {
        return Collections.emptyList();
    }

    @Override
    public String getUsage(CommandInvocation invocation, String... strings)
    {
        return descriptor.getUsage(invocation, strings);
    }

    @Override
    public String getDescription()
    {
        return this.descriptor.getDescription();
    }

    public CommandDescriptor mainDescriptor()
    {
        return descriptor;
    }

    @Override
    public Dispatcher getDispatcher()
    {
        return this.dispatcher;
    }

    @Override
    public void setDispatcher(Dispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    @Override
    public Class getOwner()
    {
        return descriptor.getOwner();
    }
}
