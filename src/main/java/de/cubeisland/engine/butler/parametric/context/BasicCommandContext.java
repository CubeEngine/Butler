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
package de.cubeisland.engine.butler.parametric.context;

import java.util.List;
import de.cubeisland.engine.butler.CommandInvocation;
import de.cubeisland.engine.butler.CommandSource;

/**
 * A CommandContext for an Invocation of a MethodicCommand
 */
public class BasicCommandContext
{
    private final CommandInvocation invocation;

    public BasicCommandContext(CommandInvocation invocation)
    {
        this.invocation = invocation;
    }

    /**
     * Returns the CommandCall
     *
     * @return the CommandCall
     */
    public CommandInvocation getInvocation()
    {
        return invocation;
    }

    /**
     * Returns the parent calls
     *
     * @return the parent calls
     */
    public List<String> getParentInvocations()
    {
        return invocation.getLabels();
    }

    /**
     * Returns the source of the commands invocation
     *
     * @return the CommandSource
     */
    public CommandSource getSource()
    {
        return invocation.getCommandSource();
    }

    /**
     * Returns whether the source is of given type
     *
     * @param clazz the type
     * @return whether the source is of given type
     */
    public boolean isSource(Class<? extends CommandSource> clazz)
    {
        return clazz.isAssignableFrom(this.getSource().getClass());
    }
}
