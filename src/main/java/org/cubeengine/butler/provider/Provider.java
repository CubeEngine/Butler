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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class Provider<T>
{
    private Map<Class<?>, T> provided = new HashMap<>();
    private Map<Class<?>, Object> ownerLookup = new HashMap<>();
    private Map<Object, List<T>> providedByOwner = new WeakHashMap<>();

    public void register(Object owner, T toRegister, Class<?>... classes)
    {
        if (owner == null)
        {
            throw new IllegalArgumentException("owner may not be null!");
        }
        for (Class clazz : classes)
        {
            provided.put(clazz, toRegister);
            ownerLookup.put(clazz, owner);
        }
        provided.put(toRegister.getClass(), toRegister);
        List<T> list = providedByOwner.get(owner);
        if (list == null)
        {
            list = new ArrayList<>();
            providedByOwner.put(owner, list);
        }
        list.add(toRegister);
    }

    public T get(Class<?> type)
    {
        return provided.get(type);
    }

    public boolean has(Class<?> type)
    {
        return resolve(type) != null;
    }

    public boolean removeAll(Object owner)
    {
        List<T> removed = providedByOwner.remove(owner);
        if (removed == null)
        {
            return false;
        }
        provided.values().removeAll(removed);
        ownerLookup.values().remove(owner);
        return true;
    }

    public boolean remove(Class<?> type)
    {
        T removed = provided.remove(type);
        ownerLookup.remove(type);
        if (removed != null)
        {
            provided.values().remove(removed);
            return true;
        }
        return false;
    }

    public Collection<T> values()
    {
        return provided.values();
    }

    public Set<Class<?>> keys()
    {
        return provided.keySet();
    }

    public T resolve(Class<?> type)
    {
        T instance = get(type);
        if (instance == null)
        {
            for (Class next : keys())
            {
                if (type.isAssignableFrom(next))
                {
                    instance = get(next);
                    if (instance != null)
                    {
                        register(ownerLookup.get(next), instance, type);
                        break;
                    }
                }
            }
        }
        return instance;
    }
}
