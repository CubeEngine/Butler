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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.command.context.ContextParser.Type;

import static java.util.Locale.ENGLISH;

public class ParsedParameters
{
    private final String[] rawArgs;
    final List<String> rawIndexed;
    final Map<String, String> rawNamed;
    private final Set<String> flags;

    int indexedCount;
    private int namedCount;
    private int flagCount;

    public final Type last;

    public ParsedParameters(String[] rawArgs, List<String> rawIndexed, Map<String, String> rawNamed, Set<String> flags,
                            Type last)
    {
        this.rawArgs = rawArgs;
        this.rawIndexed = rawIndexed;
        this.rawNamed = rawNamed;
        this.flags = flags;
        this.last = last;

        this.indexedCount = rawIndexed.size();
        this.namedCount = rawNamed.size();
        this.flagCount = flags.size();
    }

    public String[] getRawArgs()
    {
        return rawArgs;
    }

    // INDEXED PARAMETERS

    public boolean hasIndexed()
    {
        return this.indexedCount > 0;
    }

    public boolean hasIndexed(int i)
    {
        return i >= 0 && i < this.indexedCount;
    }

    public int getIndexedCount()
    {
        return this.indexedCount;
    }

    public List<String> getRawIndexed()
    {
        return new ArrayList<>(rawIndexed);
    }

    public String getString(int i)
    {
        if (this.hasIndexed(i))
        {
            return this.rawIndexed.get(i);
        }
        return null;
    }

    public String getString(int i, String def)
    {
        String value = this.getString(i);
        if (value == null)
        {
            value = def;
        }
        return value;
    }

    public String getStrings(int from)
    {
        if (!this.hasIndexed(from))
        {
            return null;
        }
        StringBuilder sb = new StringBuilder(this.getString(from));
        while (this.hasIndexed(++from))
        {
            sb.append(" ").append(this.getString(from));
        }
        return sb.toString();
    }

    // NAMED PARAMETERS

    public boolean hasNamed()
    {
        return this.namedCount > 0;
    }

    public boolean hasNamed(String name)
    {
        return this.rawNamed.containsKey(name.toLowerCase(ENGLISH));
    }

    public String getString(String name)
    {
        return this.rawNamed.get(name);
    }

    public String getString(String name, String def)
    {
        String value = this.getString(name);
        if (value == null)
        {
            value = def;
        }
        return value;
    }

    public int getNamedCount()
    {
        return namedCount;
    }

    public Map<String, String> getRawNamed()
    {
        return new LinkedHashMap<>(rawNamed);
    }

    // FLAGS

    public boolean hasFlags()
    {
        return !this.flags.isEmpty();
    }

    public boolean hasFlag(String name)
    {
        return this.flags.contains(name.toLowerCase(ENGLISH));
    }

    public boolean hasFlags(String... names)
    {
        for (String name : names)
        {
            if (!this.hasFlag(name))
            {
                return false;
            }
        }
        return true;
    }

    public int getFlagCount()
    {
        return flagCount;
    }

    public Set<String> getFlags()
    {
        return new HashSet<>(flags);
    }
}
