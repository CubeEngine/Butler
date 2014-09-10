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
package de.cubeisland.engine.command.old.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.cubeisland.engine.command.old.context.parameter.FlagParameter;
import de.cubeisland.engine.command.old.context.parameter.IndexedParameter;
import de.cubeisland.engine.command.old.context.parameter.NamedParameter;
import de.cubeisland.engine.command.old.context.parameter.ParameterGroup;

public class CtxDescriptor
{
    protected final de.cubeisland.engine.command.context.Group<? extends IndexedParameter> indexedGroup;
    protected final de.cubeisland.engine.command.context.Group<? extends NamedParameter> namedGroup;

    protected final LinkedHashMap<Integer, IndexedParameter> indexedMap = new LinkedHashMap<>();
    protected final LinkedHashMap<String, NamedParameter> namedMap = new LinkedHashMap<>();
    protected final Map<String, FlagParameter> flagMap = new HashMap<>();

    protected final de.cubeisland.engine.command.context.ArgBounds bounds;

    private CtxDescriptor()
    {
        this.indexedGroup = new ParameterGroup<IndexedParameter>();
        this.namedGroup = new ParameterGroup<NamedParameter>();
        this.bounds = new de.cubeisland.engine.command.context.ArgBounds(0, 0);
    }

    public CtxDescriptor(de.cubeisland.engine.command.context.Group<? extends IndexedParameter> indexedGroup, de.cubeisland.engine.command.context.Group<? extends NamedParameter> namedGroup,
                         List<? extends FlagParameter> flags)
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

        for (FlagParameter flag : flags)
        {
            flagMap.put(flag.getLongName().toLowerCase(), flag);
            flagMap.put(flag.getName().toLowerCase(), flag);
        }

        this.bounds = new de.cubeisland.engine.command.context.ArgBounds(indexedGroup);
    }

    public static CtxDescriptor emptyDescriptor()
    {
        return new CtxDescriptor();
    }

    public FlagParameter getFlag(String name)
    {
        return this.flagMap.get(name.toLowerCase());
    }

    public de.cubeisland.engine.command.context.Group<? extends IndexedParameter> getIndexedGroups()
    {
        return this.indexedGroup;
    }

    public de.cubeisland.engine.command.context.Group<? extends NamedParameter> getNamedGroups()
    {
        return this.namedGroup;
    }

    public Collection<FlagParameter> getFlags()
    {
        return this.flagMap.values();
    }

    public de.cubeisland.engine.command.context.ArgBounds getArgBounds()
    {
        return this.bounds;
    }

    public IndexedParameter getIndexed(int index)
    {
        return this.indexedMap.get(index);
    }

    public NamedParameter getNamed(String name)
    {
        return this.namedMap.get(name);
    }
}
