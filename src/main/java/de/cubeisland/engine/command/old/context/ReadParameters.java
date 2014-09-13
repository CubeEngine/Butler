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
package de.cubeisland.engine.command.old.context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Locale.ENGLISH;

public class ReadParameters extends ParsedParameters
{
    List<Object> indexed;
    Map<String, Object> named;

    public ReadParameters(String[] rawArgs, List<String> rawIndexed, Map<String, String> rawNamed, Set<String> flags
                          )
    {
        super(rawArgs, rawIndexed, rawNamed, flags);
    }

    public List<Object> getIndexed()
    {
        return new ArrayList<>(this.indexed);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArg(int i)
    {
        if (this.hasIndexed(i))
        {
            return (T)this.indexed.get(i);
        }
        return null;
    }

    public <T> T getArg(int index, T def)
    {
        T value = this.getArg(index);
        if (value == null)
        {
            value = def;
        }
        return value;
    }

    public Map<String, Object> getParams()
    {
        return new LinkedHashMap<>(this.named);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArg(String name)
    {
        return (T)this.named.get(name.toLowerCase(ENGLISH));
    }

    public <T> T getArg(String name, T def)
    {
        T value = this.getArg(name);
        if (value == null)
        {
            value = def;
        }
        return value;
    }
}
