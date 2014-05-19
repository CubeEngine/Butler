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

public class CommandFlag
{
    private final String name;
    private final String longName;

    private final CommandPermission permission;

    public CommandFlag(String name, String longName, CommandPermission permission)
    {
        this.name = name;
        this.longName = longName;
        this.permission = permission;
    }

    public CommandFlag(String name, String longName)
    {
        this(name, longName, null);
    }

    public String getName()
    {
        return name;
    }

    public String getLongName()
    {
        return longName;
    }

    public boolean checkPermission(Permissible permissible)
    {
        return this.permission == null || permissible == null || this.permission.hasPerm(permissible);
    }

    public CommandPermission getPermission()
    {
        return permission;
    }
}
