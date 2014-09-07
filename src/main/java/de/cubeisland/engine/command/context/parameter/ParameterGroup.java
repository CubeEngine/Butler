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
package de.cubeisland.engine.command.context.parameter;

import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.context.Group;

public class ParameterGroup<T extends Group<T>> implements Group<T>
{
    private final List<Group<T>> entries;
    private boolean isRequired;

    public ParameterGroup(List<Group<T>> entries, boolean isRequired)
    {
        this.entries = entries;
        this.isRequired = isRequired;
    }

    public ParameterGroup()
    {
        this(true);
    }

    public ParameterGroup(boolean isRequired)
    {
        this(new ArrayList<Group<T>>(), isRequired);
    }

    @Override
    public boolean isRequired()
    {
        return isRequired;
    }

    @Override
    public List<Group<T>> list()
    {
        return entries;
    }

    @Override
    public List<T> listAll()
    {
        List<T> result = new ArrayList<>();
        for (Group<T> entry : entries)
        {
            result.addAll(entry.listAll());
        }
        return result;
    }

    public void add(T toAdd)
    {
        this.entries.add(toAdd);
    }
}
