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
/**
 * This file is part of CubeEngine.
 * CubeEngine is licensed under the GNU General Public License Version 3.
 *
 * CubeEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CubeEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CubeEngine.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cubeisland.engine.command.context;

import de.cubeisland.engine.command.context.parameter.IndexedParameter;
import de.cubeisland.engine.command.context.parameter.ParameterGroup;

public class ArgBounds
{
    public static final int NO_MAX = -1;
    private final int min;
    private final int max;

    public ArgBounds(int min)
    {
        this(min, min);
    }

    public ArgBounds(int min, int max)
    {
        if (max > NO_MAX && min > max)
        {
            throw new IllegalArgumentException("The arg limit must not be greater than the minimum!");
        }
        this.min = min;
        this.max = max;
    }


    public ArgBounds(Group<? extends IndexedParameter> indexedGroup)
    {
        int tMin = 0;
        int tMax = 0;
        int n = 0;
        for (Group<? extends IndexedParameter> group : indexedGroup.list())
        {
            n++;
            if (group instanceof ParameterGroup)
            {
                ArgBounds argBounds = new ArgBounds(group);
                tMin += group.isRequired() ? argBounds.getMin() : 0;
                tMax += argBounds.getMax();
                if (argBounds.getMax() == NO_MAX)
                {
                    if (n == indexedGroup.list().size())
                    {
                        tMax = NO_MAX;
                        break;
                    }
                    throw new IllegalArgumentException("Greedy arguments are only allowed at the end!");
                }
            }
            else if (group instanceof IndexedParameter)
            {
                if (((IndexedParameter)group).getGreed() == -1)
                {
                    if (n == indexedGroup.list().size())
                    {
                        tMax = NO_MAX;
                        tMin++;
                        break;
                    }
                    throw new IllegalArgumentException("Greedy arguments are only allowed at the end!");
                }
                tMin += indexedGroup.isRequired() ? 1 : 0;
                tMax++;
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        this.min = tMin;
        this.max = tMax;
    }

    public int getMin()
    {
        return this.min;
    }

    public int getMax()
    {
        return max;
    }
}
