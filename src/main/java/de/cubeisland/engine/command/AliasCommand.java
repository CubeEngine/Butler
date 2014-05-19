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
package de.cubeisland.engine.command;

import java.util.List;

public class AliasCommand extends BaseCommand
{
    private final BaseCommand target;

    public AliasCommand(String name, BaseCommand target, BaseCommand parent)
    {
        super(target.getCommandManager(), new AliasCommandDescriptor(target, name, parent));
        this.target = target;
    }

    public BaseCommand getTarget()
    {
        return this.target;
    }

    @Override
    public CommandResult run(CommandContext context)
    {
        return this.target.run(context);
    }

    @Override
    public void help(CommandContext context)
    {
        this.target.help(context);
    }

    @Override
    public List<String> tabComplete(CommandContext context)
    {
        return this.target.tabComplete(context);
    }
}
