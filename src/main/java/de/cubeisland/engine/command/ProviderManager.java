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
package de.cubeisland.engine.command;

import java.util.List;
import de.cubeisland.engine.command.completer.Completer;
import de.cubeisland.engine.command.completer.CompleterProvider;
import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.parameter.reader.ArgumentReader;
import de.cubeisland.engine.command.parameter.reader.DefaultProvider;
import de.cubeisland.engine.command.parameter.reader.DefaultValue;
import de.cubeisland.engine.command.parameter.reader.ReaderException;
import de.cubeisland.engine.command.parameter.reader.ReaderProvider;
import de.cubeisland.engine.command.parameter.reader.SimpleEnumReader;
import de.cubeisland.engine.command.parameter.reader.SimpleListReader;
import de.cubeisland.engine.command.parameter.reader.StringArrayReader;
import de.cubeisland.engine.command.parameter.reader.StringReader;

public class ProviderManager
{
    private CompleterProvider completers = new CompleterProvider();
    private ReaderProvider readers = new ReaderProvider();
    private DefaultProvider defaults = new DefaultProvider();

    public ProviderManager()
    {
        register(this, new StringReader(), String.class);
        register(this, new SimpleListReader(","), List.class);
        register(this, new SimpleEnumReader(), Enum.class);
        register(this, new StringArrayReader(), String[].class);
    }

    public void register(Object owner, Object toRegister, Class<?>... classes)
    {
        if (toRegister instanceof ArgumentReader)
        {
            readers.register(owner, (ArgumentReader)toRegister, classes);
        }

        if (toRegister instanceof DefaultValue)
        {
            defaults.register(owner, (DefaultValue)toRegister, classes);
        }

        if (toRegister instanceof Completer)
        {
            completers.register(owner, (Completer)toRegister, classes);
        }
    }

    public void removeAll(Object owner)
    {
        readers.removeAll(owner);
        defaults.removeAll(owner);
        completers.removeAll(owner);
    }

    public boolean hasReader(Class<?> type)
    {
        return resolveReader(type) != null;
    }

    public ArgumentReader resolveReader(Class<?> type)
    {
        ArgumentReader reader = getReader(type);
        if (reader == null)
        {
            for (Class<?> next : readers.keys())
            {
                if (type.isAssignableFrom(next))
                {
                    reader = readers.get(next);
                    if (reader != null)
                    {
                        register(reader, type);
                        break;
                    }
                }
            }
        }
        return reader;
    }

    public ArgumentReader getReader(Class<?> type)
    {
        return readers.get(type);
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
        return reader.read(type, invocation);
    }

    public Object getDefault(Class<?> defaultProvider, CommandInvocation invocation)
    {
        DefaultValue def = this.defaults.get(defaultProvider);
        if (def != null)
        {
            return def.getDefault(invocation);
        }
        return null;
    }

    public Completer getCompleter(Class completerClass)
    {
        return completers.get(completerClass);
    }
}
