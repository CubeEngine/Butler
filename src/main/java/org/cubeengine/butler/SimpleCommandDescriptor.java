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
package org.cubeengine.butler;

import java.util.ArrayList;
import java.util.List;
import org.cubeengine.butler.alias.AliasConfiguration;
import org.cubeengine.butler.parameter.UsageGenerator;

public class SimpleCommandDescriptor implements CommandDescriptor, Dispatchable
{
    private String name;
    private String description;
    private UsageGenerator usageGenerator;
    private List<AliasConfiguration> aliases = new ArrayList<>();
    private Dispatcher dispatcher;

    @Override
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public List<AliasConfiguration> getAliases()
    {
        return aliases;
    }

    public void addAliases(List<AliasConfiguration> aliases)
    {
        this.aliases.addAll(aliases);
    }

    public void setUsageGenerator(UsageGenerator usageGenerator)
    {
        this.usageGenerator = usageGenerator;
    }

    @Override
    public String getUsage(CommandInvocation invocation, String... labels)
    {
        return usageGenerator.generateUsage(invocation, this, labels);
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
}
