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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.command.Completer;

public abstract class BaseParameter<T extends BaseParameter> implements Group<T>
{
    public static final String STATIC_LABEL = "\n";
    private final Class<?> type;
    private final Class<?> reader;
    private final int greed;
    private final boolean required;

    private final String valueLabel;
    private final String description;

    private final List<T> thisList;
    private final List<Group<T>> thisGroupList;
    /**
     * Contains all allowed static values mapped onto their Reader/Type Class
     */
    private final Map<String, Class<?>> staticValues = new HashMap<>();
    private Completer completer;

    @SuppressWarnings("unchecked")
    protected BaseParameter(Class<?> type, Class<?> reader, int greed, boolean required, String valueLabel,
                            String description)
    {
        this.thisList = Collections.unmodifiableList(Arrays.asList((T)this));
        thisGroupList = Collections.unmodifiableList(Arrays.asList((Group<T>)this));
        this.type = type;
        // TODO when reader String.class(default) set reader to type
        this.reader = reader; // TODO when passing List<?> type pass type as generic and reader as List
        // TODO expect(ArgumentReader.hasReader(reader), "The Parameter " + label + " has an unreadable type: " + type.getName());
        this.greed = greed;
        this.required = required;
        this.valueLabel = valueLabel;
        this.description = description;
    }

    public void addStaticReader(String value, Class reader)
    {
        if (!value.isEmpty())
        {
            this.staticValues.put(value.toLowerCase(), reader);
        }
    }

    public Class<?> getStaticReader(String arg)
    {
        return this.staticValues.get(arg);
    }

    public Set<String> getStaticValues()
    {
        return this.staticValues.keySet();
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

    public Completer getCompleter()
    {
        return completer;
    }

    public void setCompleter(Completer completer)
    {
        this.completer = completer;
    }

    public boolean isInRequiredGroup()
    {
        return true; // TODO get Group it was assigned to and group from that etc. until is false
    }
}
