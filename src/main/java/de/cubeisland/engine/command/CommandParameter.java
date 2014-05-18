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
package de.cubeisland.engine.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.cubeisland.engine.command.completer.Completer;
import de.cubeisland.engine.command.reader.ArgumentReader;

public class CommandParameter
{
    private final String name;
    private final String label;
    private final Set<String> aliases;

    private final Class<?> type;
    private final CommandPermission permission;
    private boolean required;

    private Completer completer;

    public CommandParameter(String name, String label, Class<?> type, CommandPermission permission)
    {
        if (!ArgumentReader.hasReader(type))
        {
            throw new IllegalArgumentException(
                "The named parameter '" + name + "' has an unreadable type: " + type.getName());
        }
        this.name = name;
        this.label = label.isEmpty() ? name : label;
        this.aliases = new HashSet<>(0);
        this.type = type;
        this.required = false;
        this.completer = null;
        this.permission = permission;
    }

    public CommandParameter(String name, String label, Class<?> type)
    {
        this(name, label, type, null);
    }

    public String getLabel()
    {
        return label;
    }

    public String getName()
    {
        return this.name;
    }

    public Set<String> getAliases()
    {
        return this.aliases;
    }

    public CommandParameter addAlias(String alias)
    {
        this.aliases.add(alias);
        return this;
    }

    public CommandParameter addAliases(Collection<String> aliases)
    {
        this.aliases.addAll(aliases);
        return this;
    }

    public CommandParameter addAliases(String... aliases)
    {
        for (String alias : aliases)
        {
            this.addAlias(alias);
        }
        return this;
    }

    public CommandParameter removeAlias(String alias)
    {
        this.aliases.remove(alias);
        return this;
    }

    public CommandParameter removeAliases(Collection<String> aliases)
    {
        this.aliases.removeAll(aliases);
        return this;
    }

    public CommandParameter removeAliases(String... aliases)
    {
        for (String alias : aliases)
        {
            this.removeAlias(alias);
        }
        return this;
    }

    public Class<?> getType()
    {
        return this.type;
    }

    public boolean isRequired()
    {
        return this.required;
    }

    public CommandParameter setRequired(boolean required)
    {
        this.required = required;
        return this;
    }

    public Completer getCompleter()
    {
        return this.completer;
    }

    public CommandParameter setCompleter(Completer completer)
    {
        this.completer = completer;
        return this;
    }

    public boolean checkPermission(Permissible permissible)
    {
        return this.permission == null || permissible == null || this.permission.isAuthorized(permissible);
    }

    public CommandPermission getPermission()
    {
        return permission;
    }
}
