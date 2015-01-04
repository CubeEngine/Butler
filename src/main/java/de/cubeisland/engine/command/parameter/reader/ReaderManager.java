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
package de.cubeisland.engine.command.parameter.reader;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.parameter.Parameter;

// TODO add Owner to registerReader so it is possible to remove all Readers from an owner
public class ReaderManager
{
    private final Map<Class<?>, ArgumentReader> readers = new ConcurrentHashMap<>();
    private final Map<Class, DefaultProvider> defaultProviders = new ConcurrentHashMap<>();

    public void registerDefaultReader()
    {
        registerReader(new StringReader(), String.class);
        registerReader(new SimpleListReader(","), List.class);
        registerReader(new SimpleEnumReader(), Enum.class);
    }

    public void registerReader(ArgumentReader reader, Class<?>... classes)
    {
        for (Class c : classes)
        {
            readers.put(c, reader);
        }
        readers.put(reader.getClass(), reader);
        if (reader instanceof DefaultProvider)
        {
            defaultProviders.put(reader.getClass(), (DefaultProvider)reader);
            for (Class<?> c : classes)
            {
                defaultProviders.put(c, (DefaultProvider)reader);
            }
        }
    }

    public ArgumentReader getReader(Class<?> type)
    {
        return readers.get(type);
    }

    public ArgumentReader resolveReader(Class<?> type)
    {
        ArgumentReader reader = getReader(type);
        if (reader == null)
        {
            for (Class<?> next : readers.keySet())
            {
                if (type.isAssignableFrom(next))
                {
                    reader = readers.get(next);
                    if (reader != null)
                    {
                        registerReader(reader, type);
                        break;
                    }
                }
            }
        }
        return reader;
    }

    public boolean hasReader(Class<?> type)
    {
        return resolveReader(type) != null;
    }

    public void removeReader(Class type)
    {
        Iterator<Map.Entry<Class<?>, ArgumentReader>> it = readers.entrySet().iterator();

        Map.Entry<Class<?>, ArgumentReader> entry;
        while (it.hasNext())
        {
            entry = it.next();
            if (entry.getKey() == type || entry.getValue().getClass() == type)
            {
                it.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Object read(Parameter param, CommandInvocation invocation) throws ReaderException
    {
        return this.read(param.getReaderType(), param.getType(), invocation);
    }

    public Object read(Class<?> readerClass, Class<?> type, CommandInvocation invocation)
    {
        ArgumentReader reader = resolveReader(readerClass);
        if (reader == null)
        {
            throw new IllegalArgumentException("No reader found for " + readerClass.getName() + "!");
        }
        return reader.read(this, type, invocation);
    }

    public Object getDefault(Class<?> defaultProvider, CommandInvocation invocation)
    {
        return this.defaultProviders.get(defaultProvider).getDefault(invocation);
    }
}
