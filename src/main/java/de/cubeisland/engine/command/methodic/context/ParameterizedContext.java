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
package de.cubeisland.engine.command.methodic.context;

import de.cubeisland.engine.command.parameter.FlagParameter;
import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.parameter.ParsedParameter;
import de.cubeisland.engine.command.parameter.ParsedParameters;
import de.cubeisland.engine.command.parameter.property.FixedPosition;
import de.cubeisland.engine.command.parameter.property.FixedValues;
import de.cubeisland.engine.command.tokenized.TokenizedInvocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A context with parameterized values
 */
public class ParameterizedContext extends BaseCommandContext
{
    private final Map<Integer, ParsedParameter> positional = new HashMap<>();
    private final Map<String, ParsedParameter> nameBased = new HashMap<>();
    private final Map<String, ParsedParameter> flags = new HashMap<>();

    public ParameterizedContext(TokenizedInvocation call)
    {
        super(call);

        for (ParsedParameter parsed : call.valueFor(ParsedParameters.class))
        {
            Parameter parameter = parsed.getParameter();
            if (parameter instanceof FlagParameter)
            {
                flags.put(((FlagParameter) parameter).name(), parsed);
            }
            else
            {
                Integer pos = parameter.valueFor(FixedPosition.class);
                String[] fixedValues = parameter.valueFor(FixedValues.class);
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

    /**
     * Returns whether the context has any positional parameters
     *
     * @return whether the context has any positional parameters
     */
    public final boolean hasPositional()
    {
        return !this.positional.isEmpty();
    }

    /**
     * Returns whether the context has a positional parameter for given index
     *
     * @param index the index
     * @return whether the context has a positional parameter for given index
     */
    public final boolean hasPositional(int index)
    {
        return this.positional.containsKey(index);
    }

    /**
     * Returns the amount of positional parameters
     *
     * @return the amount of positional parameters
     */
    public final int getPositionalCount()
    {
        return this.positional.size();
    }

    /**
     * Returns the unparsed positional parameters as {@code List<String>}
     *
     * @return the unparsed positional parameters as {@code List<String>}
     */
    public final List<String> getRawPositional()
    {
        List<String> result = new ArrayList<>();
        for (ParsedParameter parsed : this.positional.values())
        {
            result.add(parsed.getRawValue());
        }
        return result;
    }

    /**
     * Returns the unparsed positional parameter for given index
     *
     * @param index the index
     * @return the unparsed positional parameter for given index
     */
    public final String getString(int index)
    {
        ParsedParameter parsed = this.positional.get(index);
        if (parsed != null)
        {
            return parsed.getRawValue();
        }
        return null;
    }

    /**
     * Returns the unparsed positional parameter for given index or the default value if not found
     *
     * @param index the index
     * @param def   the default value
     * @return the unparsed positional parameter for given index or {@code def} if not found
     */
    public final String getString(int index, String def)
    {
        String result = this.getString(index);
        if (result == null)
        {
            result = def;
        }
        return result;
    }

    /**
     * Returns the unparsed positional parameters joined by {@code " "} starting with given index until the last
     *
     * @param from the index to start at
     * @return the unparsed positional parameters starting with given index
     */
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

    /**
     * Returns whether the context has any named parameters
     *
     * @return whether the context has any named parameters
     */
    public final boolean hasNamed()
    {
        return !this.nameBased.isEmpty();
    }

    /**
     * Returns whether the context has a named parameter for given name
     *
     * @param name the name
     * @return whether the context has a named parameter for given name
     */
    public final boolean hasNamed(String name)
    {
        return this.nameBased.containsKey(name);
    }

    /**
     * Returns the unparsed named parameter for given name
     *
     * @param name the name
     * @return the unparsed named parameter for given name
     */
    public final String getString(String name)
    {
        ParsedParameter parsed = this.nameBased.get(name);
        if (parsed != null)
        {
            return parsed.getRawValue();
        }
        return null;
    }

    /**
     * Returns the unparsed named parameter for given name or the default value if not found
     *
     * @param name the name
     * @param def  the default value
     * @return the unparsed named parameter for given name or {@code def} if not found
     */
    public final String getString(String name, String def)
    {
        String result = this.getString(name);
        if (result == null)
        {
            result = def;
        }
        return result;
    }

    /**
     * Returns the amount of named parameters
     *
     * @return the amount of named parameters
     */
    public final int getNamedCount()
    {
        return this.nameBased.size();
    }

    /**
     * Returns the unparsed named parameters as {@code Map<String,String>}
     *
     * @return the unparsed named parameters
     */
    public final Map<String, String> getRawNamed()
    {
        Map<String, String> map = new HashMap<>();
        for (Entry<String, ParsedParameter> entry : this.nameBased.entrySet())
        {
            map.put(entry.getKey(), entry.getValue().getRawValue());
        }
        return map;
    }

    /**
     * Returns whether the context has any flags
     *
     * @return whether the context has any flags
     */
    public final boolean hasFlags()
    {
        return !this.flags.isEmpty();
    }

    /**
     * Returns whether the context has the flag
     *
     * @param name the name of the flag to check
     * @return whether the context has the flag
     */
    public final boolean hasFlag(String name)
    {
        return this.flags.containsKey(name);
    }

    /**
     * Returns whether the context has all the flags given
     *
     * @param names the names of the flags to check
     * @return whether the context has the flags
     */
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

    /**
     * Returns the amount of flags this context has
     *
     * @return the amount of flags this context has
     */
    public final int getFlagCount()
    {
        return this.flags.size();
    }

    /**
     * Returns the parsed positional parameter at given index
     *
     * @param index the index
     * @param <T>   the Type of the parsed parameter
     * @return the parsed positional parameter
     */
    @SuppressWarnings("unchecked")
    public final <T> T get(int index)
    {
        if (this.hasPositional(index))
        {
            return (T) this.positional.get(index);
        }
        return null;
    }

    /**
     * Returns the parsed positional parameter at given index or the default value if not found
     *
     * @param index the index
     * @param def   the default value
     * @param <T>   the Type of the parsed parameter
     * @return the parsed positional parameter at given index or {@code def} if not found
     */
    public final <T> T get(int index, T def)
    {
        T result = this.get(index);
        if (result == null)
        {
            result = def;
        }
        return result;
    }

    /**
     * Returns the parsed named parameter for given name
     *
     * @param name the name
     * @param <T>  the Type of the parsed parameter
     * @return the parsed named parameter for given name
     */
    @SuppressWarnings("unchecked")
    public final <T> T get(String name)
    {
        if (this.hasNamed(name))
        {
            return (T) this.nameBased.get(name);
        }
        return null;
    }

    /**
     * Returns the parsed named parameter for given name or the default value if not found
     *
     * @param name the name
     * @param def  the default value
     * @param <T>  the Type of the parsed parameter
     * @return the parsed named parameter for the given name or {@code def} if not found
     */
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
