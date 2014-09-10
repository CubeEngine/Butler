package de.cubeisland.engine.command.parameter.reader;

import de.cubeisland.engine.command.old.context.reader.SimpleListReader;
import de.cubeisland.engine.command.old.context.reader.StringReader;
import de.cubeisland.engine.command.old.exception.ReaderException;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReaderManager
{
    private final Map<Class<?>, ArgumentReader> READERS = new ConcurrentHashMap<>();

    public void registerDefaultReader()
    {
        registerReader(new StringReader(), String.class);
        registerReader(new SimpleListReader(","), List.class);
    }

    public void registerReader(ArgumentReader reader, Class<?>... classes)
    {
        for (Class c : classes)
        {
            READERS.put(c, reader);
        }
        READERS.put(reader.getClass(), reader);
    }

    public ArgumentReader getReader(Class<?> type)
    {
        return READERS.get(type);
    }

    public ArgumentReader resolveReader(Class<?> type)
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

    public boolean hasReader(Class<?> type)
    {
        return resolveReader(type) != null;
    }

    public void removeReader(Class type)
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
    public Object read(Class<?> readerClass, Class<?> type, String string, Locale locale) throws ReaderException
    {
        ArgumentReader reader = resolveReader(readerClass);
        if (reader == null)
        {
            throw new IllegalArgumentException("No reader found for " + type.getName() + "!");
        }
        return reader.read(this, type, string, locale);
    }
}
