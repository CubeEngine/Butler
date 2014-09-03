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
package de.cubeisland.engine.command.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CtxDescriptor
{
    protected final Group<? extends IndexedParameter> indexedGroup;
    protected final Group<? extends NamedParameter> namedGroup;

    protected final LinkedHashMap<Integer, IndexedParameter> indexedMap = new LinkedHashMap<>();
    protected final LinkedHashMap<String, NamedParameter> namedMap = new LinkedHashMap<>();
    protected final Map<String, Flag> flagMap = new HashMap<>();

    protected final ArgBounds bounds;

    protected CtxDescriptor(Group<? extends IndexedParameter> indexedGroup, Group<? extends NamedParameter> namedGroup,
                            List<? extends Flag> flags)
    {
        this.indexedGroup = indexedGroup;
        this.namedGroup = namedGroup;

        for (IndexedParameter indexed : indexedGroup.listAll())
        {
            indexedMap.put(indexedMap.size(), indexed);
        }

        for (NamedParameter named : namedGroup.listAll())
        {
            namedMap.put(named.getName().toLowerCase(), named);
            for (String alias : named.getAliases())
            {
                namedMap.put(alias.toLowerCase(), named);
            }
        }

        for (Flag flag : flags)
        {
            flagMap.put(flag.getLongName().toLowerCase(), flag);
            flagMap.put(flag.getName().toLowerCase(), flag);
        }

        this.bounds = new ArgBounds(indexedGroup);
    }


    public Flag getFlag(String name)
    {
        return this.flagMap.get(name.toLowerCase());
    }

    public NamedParameter getNamed(String name)
    {
        return this.namedMap.get(name.toLowerCase());
    }

    public Group<? extends IndexedParameter> getIndexedGroups()
    {
        return this.indexedGroup;
    }

    public Group<? extends NamedParameter> getNamedGroups()
    {
        return this.namedGroup;
    }

    public Collection<Flag> getFlags()
    {
        return this.flagMap.values();
    }

    public ArgBounds getArgBounds()
    {
        return this.bounds;
    }

    public IndexedParameter getIndexed(int index)
    {
        return this.indexedMap.get(index);
    }
}
