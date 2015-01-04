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
package de.cubeisland.engine.command.alias;

import java.util.List;

import de.cubeisland.engine.command.CommandBase;
import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.CommandInvocation;

public class AliasCommand implements CommandBase
{
    private AliasConfiguration config;
    private final CommandBase target;
    private CommandDescriptor descriptor;

    public AliasCommand(AliasConfiguration config, CommandBase target)
    {
        this.config = config;
        this.target = target;
        this.descriptor = new AliasDescriptor(config.getName(), target.getDescriptor());
    }

    @Override
    public boolean execute(CommandInvocation invocation)
    {
        // TODO prefix & suffix
        return target.execute(invocation);
    }

    @Override
    public CommandDescriptor getDescriptor()
    {
        return this.descriptor;
    }

    @Override
    public List<String> getSuggestions(CommandInvocation invocation)
    {
        return target.getSuggestions(invocation);
    }

    public CommandBase getTarget()
    {
        return target;
    }
}
