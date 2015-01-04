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
package de.cubeisland.engine.command.filter;

import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.CommandSource;

/**
 * Allows to run a command if the CommandSource is of an allowed type
 */
public class SourceFilter implements Filter
{
    private Class<? extends CommandSource>[] sources;
    private String msg;

    /**
     * Creates a new SourceFilter
     *
     * @param sources the allowed sources
     * @param msg     the message
     */
    public SourceFilter(Class<? extends CommandSource>[] sources, String msg)
    {
        this.sources = sources;
        this.msg = msg;
    }

    @Override
    public void run(CommandInvocation invocation) throws FilterException
    {
        boolean restrict = true;
        for (Class<? extends CommandSource> clazz : this.sources)
        {
            if (clazz.isAssignableFrom(invocation.getCommandSource().getClass()))
            {
                restrict = false;
            }
        }
        if (restrict)
        {
            throw new RestrictedSourceException(this.msg, this.sources);
        }
    }
}
