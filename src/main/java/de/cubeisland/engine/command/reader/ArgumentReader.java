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
package de.cubeisland.engine.command.reader;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;

import de.cubeisland.engine.command.BaseCommandSender;
import de.cubeisland.engine.command.reader.readers.BooleanReader;
import de.cubeisland.engine.command.reader.readers.ByteReader;
import de.cubeisland.engine.command.reader.readers.DifficultyReader;
import de.cubeisland.engine.command.reader.readers.DoubleReader;
import de.cubeisland.engine.command.reader.readers.DyeColorReader;
import de.cubeisland.engine.command.reader.readers.EnchantmentReader;
import de.cubeisland.engine.command.reader.readers.EntityTypeReader;
import de.cubeisland.engine.command.reader.readers.EnvironmentReader;
import de.cubeisland.engine.command.reader.readers.FloatReader;
import de.cubeisland.engine.command.reader.readers.IntReader;
import de.cubeisland.engine.command.reader.readers.IntegerOrAllReader;
import de.cubeisland.engine.command.reader.readers.ItemStackReader;
import de.cubeisland.engine.command.reader.readers.LogLevelReader;
import de.cubeisland.engine.command.reader.readers.LongReader;
import de.cubeisland.engine.command.reader.readers.OfflinePlayerReader;
import de.cubeisland.engine.command.reader.readers.ProfessionReader;
import de.cubeisland.engine.command.reader.readers.ShortReader;
import de.cubeisland.engine.command.reader.readers.StringReader;
import de.cubeisland.engine.command.reader.readers.UserListOrAllReader;
import de.cubeisland.engine.command.reader.readers.UserListReader;
import de.cubeisland.engine.command.reader.readers.UserOrAllReader;
import de.cubeisland.engine.command.reader.readers.UserReader;
import de.cubeisland.engine.command.reader.readers.WorldReader;
import de.cubeisland.engine.command.reader.readers.WorldTypeReader;
import de.cubeisland.engine.core.Core;
import de.cubeisland.engine.core.user.User;
import de.cubeisland.engine.command.exception.InvalidArgumentException;
import sun.util.logging.resources.logging.LogLevel;

public abstract class ArgumentReader
{
    // TODO unregister reader from a module!!! else memory leakage
    private static final Map<Class<?>, ArgumentReader> READERS = new ConcurrentHashMap<>();

    public static void init(Core core)
    {
        registerReader(new BooleanReader(core), Boolean.class, boolean.class);
        registerReader(new ByteReader(), Byte.class, byte.class);
        registerReader(new ShortReader(), Short.class, short.class);
        registerReader(new IntReader(), Integer.class, int.class);
        registerReader(new LongReader(), Long.class, long.class);
        registerReader(new FloatReader(), Float.class, float.class);
        registerReader(new DoubleReader(), Double.class, double.class);
        registerReader(new StringReader(), String.class);

        registerReader(new IntegerOrAllReader()); // "*" or Integer.class
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
            Class<?> next;
            Iterator<Class<?>> it = READERS.keySet().iterator();
            while (it.hasNext())
            {
                next = it.next();
                if (type.isAssignableFrom(next))
                {
                    reader = READERS.get(next);
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

    public static <T> T read(Class<T> clazz, String string, BaseCommandSender sender) throws InvalidArgumentException
    {
        return read(clazz, string, sender.getLocale());
    }

    @SuppressWarnings("unchecked")
    public static <T> T read(Class<T> type, String string, Locale locale) throws InvalidArgumentException
    {
        ArgumentReader reader = resolveReader(type);
        if (reader == null)
        {
            throw new IllegalStateException("No reader found for " + type.getName() + "!");
        }
        return (T)reader.read(string, locale);
    }

    /**
     * @param arg an string
     *
     * @return the number of arguments paired with the value that got read from the input array
     */
    public abstract Object read(String arg, Locale locale) throws InvalidArgumentException;
}
