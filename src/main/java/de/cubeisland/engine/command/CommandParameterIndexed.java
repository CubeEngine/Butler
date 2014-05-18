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

import de.cubeisland.engine.command.completer.Completer;
import de.cubeisland.engine.command.reader.ArgumentReader;

public class CommandParameterIndexed
{
    /**
     * The display label for the indexed parameter
     */
    private final String[] labels;
    private final Class<?>[] types;
    private final int count;
    private final boolean groupRequired;
    private final CommandPermission permission;
    private final boolean required;

    private Completer completer;

    public CommandParameterIndexed(String[] labels, Class<?>[] types, boolean groupRequiered, boolean required,
                                   int count, CommandPermission permission)
    {
        int i = 0;
        for (Class<?> type : types)
        {
            if (!ArgumentReader.hasReader(type))
            {
                throw new IllegalArgumentException(
                    "The indexed parameter '" + labels[0] + "(" + i + ")' has an unreadable type: " + type.getName());
            }
            i++;
        }
        this.labels = labels;
        this.types = types;
        this.groupRequired = groupRequiered;
        this.required = required;
        this.count = count;
        this.permission = permission;
    }

    public static CommandParameterIndexed greedyIndex()
    {
        return new CommandParameterIndexed(new String[]{"0"}, new Class[]{String.class}, false, false, -1, null);
    }

    public static CommandParameterIndexed emptyIndex(String label)
    {
        return new CommandParameterIndexed(new String[]{label}, new Class[]{String.class}, false, false, 1, null);
    }

    public int getCount()
    {
        return count;
    }

    public String[] getLabels()
    {
        return labels;
    }

    public Class<?>[] getType()
    {
        return types;
    }

    public Completer getCompleter()
    {
        return completer;
    }

    public void setCompleter(Completer completer)
    {
        this.completer = completer;
    }

    public boolean isGroupRequired()
    {
        return groupRequired;
    }

    public boolean isRequired()
    {
        return required;
    }
}
