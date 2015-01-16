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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import de.cubeisland.engine.command.util.property.Property;

/**
 * Contains a List of Filters
 */
public class Filters implements Property<Filters>, Iterable<Filter>
{
    private List<Filter> filters = new LinkedList<>();

    @Override
    public Filters value()
    {
        return this;
    }

    public void addFilter(Filter filter)
    {
        this.filters.add(filter);
    }

    public void removeFilter(Class<? extends Filter> clazz)
    {
        Iterator<Filter> it = filters.iterator();
        while (it.hasNext())
        {
            if (it.next().getClass().equals(clazz))
            {
                it.remove();
            }
        }
    }

    @Override
    public Iterator<Filter> iterator()
    {
        return this.filters.iterator();
    }
}
