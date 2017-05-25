/*
 * The MIT License
 * Copyright © 2014 Cube Island
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
package org.cubeengine.butler.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.ContextValue;
import org.cubeengine.butler.SourceRestrictedContextValue;
import org.cubeengine.butler.builder.CommandBuilder;
import org.cubeengine.butler.completer.Completer;
import org.cubeengine.butler.exception.CompositeExceptionHandler;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parameter.argument.ArgumentParser;
import org.cubeengine.butler.parameter.argument.DefaultValue;
import org.cubeengine.butler.parameter.argument.ReaderException;
import org.cubeengine.butler.parameter.argument.SimpleEnumParser;
import org.cubeengine.butler.parameter.argument.SimpleListParser;
import org.cubeengine.butler.parameter.argument.StringArrayParser;
import org.cubeengine.butler.parameter.argument.StringParser;
import org.cubeengine.butler.parametric.context.BasicCommandContext;
import org.cubeengine.butler.parametric.context.BasicCommandContextValue;

public class ProviderManager
{
    private final SourceRestrictedContextValue sourceContext = new SourceRestrictedContextValue();

    private CompleterProvider completers = new CompleterProvider();
    private ParserProvider readers = new ParserProvider();
    private DefaultProvider defaults = new DefaultProvider();
    private ContextProvider contexts = new ContextProvider();

    private CompositeExceptionHandler exceptionHandler = new CompositeExceptionHandler();

    private Map<Class, CommandBuilder> builders = new HashMap<>();

    public ProviderManager()
    {
        register(this, new StringParser(), String.class);
        register(this, new SimpleListParser(","), List.class);
        register(this, new SimpleEnumParser(), Enum.class);
        register(this, new StringArrayParser(), String[].class);

        register(this, new BasicCommandContextValue(), BasicCommandContext.class);
    }

    /**
     * Registers ArgumentReader, DefaultValue, Completer or ContextValue
     * @param owner
     * @param toRegister
     * @param classes
     */
    public void register(Object owner, Object toRegister, Class<?>... classes)
    {
        if (toRegister instanceof ArgumentParser)
        {
            readers.register(owner, (ArgumentParser)toRegister, classes);
        }

        if (toRegister instanceof DefaultValue)
        {
            defaults.register(owner, (DefaultValue)toRegister, classes);
        }

        if (toRegister instanceof Completer)
        {
            completers.register(owner, (Completer)toRegister, classes);
        }

        if (toRegister instanceof ContextValue)
        {
            contexts.register(owner, (ContextValue)toRegister, classes);
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

    public ArgumentParser resolveReader(Class<?> type)
    {
        ArgumentParser reader = getReader(type);
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

    public ArgumentParser getReader(Class<?> type)
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
        ArgumentParser reader = resolveReader(readerClass);
        if (reader == null)
        {
            throw new IllegalArgumentException("No reader found for " + readerClass.getName() + "!");
        }
        return reader.parse(type, invocation);
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

    public CompositeExceptionHandler getExceptionHandler()
    {
        return exceptionHandler;
    }

    /**
     * Returns the requested context or null if not found
     * @param clazz the context class
     * @param commandInvocation the commandinvocation
     * @param <T> the context Type
     * @return the requested context or null if not found
     */
    public <T> T getContext(Class<T> clazz, CommandInvocation commandInvocation)
    {
        ContextValue contextValue = contexts.get(clazz);
        if (contextValue == null)
        {
            contextValue = sourceContext;
        }
        @SuppressWarnings("unchecked")
        T val = (T)contextValue.getContext(commandInvocation, clazz);
        return val;
    }

    public <T> void registerBuilder(Class<T> clazz, CommandBuilder<T> builder)
    {
        builders.put(clazz, builder);
    }

    @SuppressWarnings("unchecked")
    public <T> CommandBuilder<T> getBuilder(Class<T> clazz)
    {
        return builders.get(clazz);
    }
}
