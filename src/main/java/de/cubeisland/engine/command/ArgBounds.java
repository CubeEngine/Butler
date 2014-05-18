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

    public ArgBounds(List<CommandParameterIndexed> indexed)
    {
        int tMin = 0;
        int tMax = 0;
        for (int i = 0; i < indexed.size(); i++)
        {
            CommandParameterIndexed indexedParam = indexed.get(i);
            if (indexedParam.getCount() == -1)
            {
                if (i + 1 == indexed.size())
                {
                    tMax = NO_MAX;
                    if (indexedParam.isGroupRequired())
                    {
                        tMin++;
                    }
                    break;
                }
                throw new IllegalArgumentException("Greedy arguments are only allowed at the end!");
            }
            if (indexedParam.isGroupRequired())
            {
                tMin += indexedParam.getCount();
                if (!indexedParam.isRequired())
                {
                    tMin -= 1;
                }
            }
            tMax += indexedParam.getCount();
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

    public boolean inBounds(int n)
    {
        if (n < this.min)
        {
            return false;
        }
        if (this.max > NO_MAX && n > this.max)
        {
            return false;
        }
        return true;
    }
}
