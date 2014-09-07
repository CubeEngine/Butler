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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.command.Completer;
import de.cubeisland.engine.command.context.Group;

public abstract class BaseParameter<T extends BaseParameter> implements Group<T>
{
    public static final String STATIC_LABEL = "\n";
    public static final int INFINITE_GREED = -1;

    private final List<T> thisList;
    private final List<Group<T>> thisGroupList;

    // package private so BaseParameterFactory can access/set the fields
    final Map<String, Class<?>> staticReaders = new HashMap<>();
    Class<?> type;
    Class<?> reader;
    int greed;
    boolean required;
    String valueLabel;
    String description;
    Completer<?> completer;

    @SuppressWarnings("unchecked")
    protected BaseParameter()
    {
        this.thisList = Collections.unmodifiableList(Arrays.asList((T)this));
        this.thisGroupList = Collections.unmodifiableList(Arrays.asList((Group<T>)this));
    }

    public Class<?> getStaticReader(String arg)
    {
        return this.staticReaders.get(arg);
    }

    public Set<String> getStaticReaders()
    {
        return this.staticReaders.keySet();
    }

    public Class<?> getType()
    {
        return type;
    }

    public Class<?> getReader()
    {
        return reader;
    }

    public int getGreed()
    {
        return greed;
    }

    @Override
    public boolean isRequired()
    {
        return required;
    }

    public String getValueLabel()
    {
        return valueLabel;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public List<Group<T>> list()
    {
        return this.thisGroupList;
    }

    @Override
    public List<T> listAll()
    {
        return this.thisList;
    }

    public Completer<?> getCompleter()
    {
        return completer;
    }

    public boolean isInRequiredGroup()
    {
        return true; // TODO get Group it was assigned to and group from that etc. until is false
    }
}
