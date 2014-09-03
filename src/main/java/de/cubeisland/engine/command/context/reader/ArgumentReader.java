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
package de.cubeisland.engine.command.context.reader;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.cubeisland.engine.command.exception.ReaderException;

public abstract class ArgumentReader
{
    private static final Map<Class<?>, ArgumentReader> READERS = new ConcurrentHashMap<>();

    public abstract Object read(Class type, String arg, Locale locale) throws ReaderException;

    static
    {
        registerReader(new StringReader(), String.class);
        registerReader(new SimpleListReader(","), List.class);
    }

    public static void registerReader(ArgumentReader reader, Class<?>... classes)
    {
        for (Class c : classes)
        {
            READERS.put(c, reader);
        }
        READERS.put(reader.getClass(), reader);
    }

    public static ArgumentReader getReader(Class<?> type)
    {
        return READERS.get(type);
    }

    public static ArgumentReader resolveReader(Class<?> type)
    {
        ArgumentReader reader = getReader(type);
        if (reader == null)
        {
            for (Class<?> next : READERS.keySet()) {
                if (type.isAssignableFrom(next)) {
                    reader = READERS.get(next);
                    if (reader != null) {
                        registerReader(reader, type);
                        break;
                    }
                }
            }
        }
        return reader;
    }

    public static boolean hasReader(Class<?> type)
    {
        return resolveReader(type) != null;
    }

    public static void removeReader(Class type)
    {
        Iterator<Map.Entry<Class<?>, ArgumentReader>> it = READERS.entrySet().iterator();

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
    public static Object read(Class<?> readerClass, Class<?> type, String string, Locale locale) throws ReaderException
    {
        ArgumentReader reader = resolveReader(readerClass);
        if (reader == null)
        {
            throw new IllegalArgumentException("No reader found for " + type.getName() + "!");
        }
        return reader.read(type, string, locale);
    }
}
