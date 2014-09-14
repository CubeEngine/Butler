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
package de.cubeisland.engine.command.parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.parameter.property.FixedPosition;
import de.cubeisland.engine.command.parameter.property.FixedValues;

public class ParameterizedContext extends CommandContext
{
    private final Map<Integer, ParsedParameter> positional = new HashMap<>();
    private final Map<String, ParsedParameter> nameBased = new HashMap<>();
    private final Map<String, ParsedParameter> flags = new HashMap<>();

    public ParameterizedContext(CommandCall call, List<String> parentCalls)
    {
        super(call, parentCalls);

        for (ParsedParameter parsed : call.propertyValue(ParsedParameters.class))
        {
            Parameter parameter = parsed.getParameter();
            if (parameter instanceof FlagParameter)
            {
                flags.put(((FlagParameter)parameter).name(), parsed);
            }
            else
            {
                Integer pos = parameter.propertyValue(FixedPosition.class);
                String[] fixedValues = parameter.propertyValue(FixedValues.class);
                if (fixedValues == null && pos == null)
                {
                    throw new IllegalStateException(
                        "Cannot assign parameter that is neither positional nor has fixed values");
                }
                if (fixedValues != null)
                {
                    nameBased.put(fixedValues[0], parsed);
                }
                if (pos != null)
                {
                    positional.put(pos, parsed);
                }
            }
        }
    }

    public final boolean hasPositional()
    {
        return !this.positional.isEmpty();
    }

    public final boolean hasPositional(int index)
    {
        return this.positional.containsKey(index);
    }

    public final int getPositionalCount()
    {
        return this.positional.size();
    }

    public final List<String> getRawPositional()
    {
        List<String> result = new ArrayList<>();
        for (ParsedParameter parsed : this.positional.values())
        {
            result.add(parsed.getRawValue());
        }
        return result;
    }

    public final String getString(int index)
    {
        ParsedParameter parsed = this.positional.get(index);
        if (parsed != null)
        {
            return parsed.getRawValue();
        }
        return null;
    }

    public final String getString(int index, String def)
    {
        String result = this.getString(index);
        if (result == null)
        {
            result = def;
        }
        return result;
    }

    public final String getStrings(int from)
    {
        if (!this.hasPositional(from))
        {
            return null;
        }
        StringBuilder sb = new StringBuilder(this.getString(from));
        while (this.hasPositional(++from))
        {
            sb.append(" ").append(this.getString(from));
        }
        return sb.toString();
    }

    public final boolean hasNamed()
    {
        return !this.nameBased.isEmpty();
    }

    public final boolean hasNamed(String name)
    {
        return this.nameBased.containsKey(name);
    }

    public final String getString(String name)
    {
        ParsedParameter parsed = this.nameBased.get(name);
        if (parsed != null)
        {
            return parsed.getRawValue();
        }
        return null;
    }

    public final String getString(String name, String def)
    {
        String result = this.getString(name);
        if (result == null)
        {
            result = def;
        }
        return result;
    }

    public final int getNamedCount()
    {
        return this.nameBased.size();
    }

    public final Map<String, String> getRawNamed()
    {
        Map<String, String> map = new HashMap<>();
        for (Entry<String, ParsedParameter> entry : this.nameBased.entrySet())
        {
            map.put(entry.getKey(), entry.getValue().getRawValue());
        }
        return map;
    }

    public final boolean hasFlags()
    {
        return !this.flags.isEmpty();
    }

    public final boolean hasFlag(String name)
    {
        return this.flags.containsKey(name);
    }

    public final boolean hasFlags(String... names)
    {
        for (String name : names)
        {
            if (!this.hasFlag(name))
            {
                return false;
            }
        }
        return true;
    }

    public final int getFlagCount()
    {
        return this.flags.size();
    }

    @SuppressWarnings("unchecked")
    public final <T> T get(int index)
    {
        if (this.hasPositional(index))
        {
            return (T)this.positional.get(index);
        }
        return null;
    }

    public final <T> T get(int index, T def)
    {
        T result = this.get(index);
        if (result == null)
        {
            result = def;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public final <T> T get(String name)
    {
        if (this.hasNamed(name))
        {
            return (T)this.nameBased.get(name);
        }
        return null;
    }
    
    public final <T> T get(String name, T def)
    {
        T result = this.get(name);
        if (result == null)
        {
            result = def;
        }
        return result;
    }
}
