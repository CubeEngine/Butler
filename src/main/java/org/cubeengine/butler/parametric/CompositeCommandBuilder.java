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

import java.util.Arrays;
import java.util.List;
import org.cubeengine.butler.builder.CommandBuilder;

public class CompositeCommandBuilder<OriginT> implements CommandBuilder<BasicParametricCommand, OriginT>
{
    private final List<CommandBuilder<BasicParametricCommand, OriginT>> list;

    public CompositeCommandBuilder(List<CommandBuilder<BasicParametricCommand, OriginT>> list)
    {
        this.list = list;
    }

    @SafeVarargs
    public CompositeCommandBuilder(CommandBuilder<BasicParametricCommand, OriginT>... builders)
    {
        this(Arrays.asList(builders));
    }

    @Override
    public BasicParametricCommand buildCommand(OriginT origin)
    {
        for (CommandBuilder<BasicParametricCommand, OriginT> builder : list)
        {
            BasicParametricCommand command = builder.buildCommand(origin);
            if (command != null)
            {
                return command;
            }
        }
        return null;
    }

    @Override
    public boolean isApplicable(OriginT originT)
    {
        for (CommandBuilder<BasicParametricCommand, OriginT> builder : list)
        {
            if (builder.isApplicable(originT))
            {
                return true;
            }
        }
        return false;
    }
}
