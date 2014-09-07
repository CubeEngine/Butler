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
package de.cubeisland.engine.command.context;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.command.BaseCommand;
import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.context.ContextParser.Type;

public class CommandContext<CommandT extends BaseCommand, SourceT extends CommandSource> extends ReadParameters
{
    private final CommandT command;
    private final SourceT source;
    private final List<String> labels;
    private final String label;

    public CommandContext(String[] rawArgs, List<String> rawIndexed, Map<String, String> rawNamed, Set<String> flags,
                          Type last, CommandT command, List<String> labels, SourceT source)
    {
        super(rawArgs, rawIndexed, rawNamed, flags, last);
        this.command = command;
        this.labels = Collections.unmodifiableList(labels);
        this.label = labels.get(labels.size() - 1);
        this.source = source;
    }

    public CommandT getCommand()
    {
        return command;
    }

    public String getLabel()
    {
        return this.label;
    }

    public List<String> getLabels()
    {
        return labels;
    }

    public SourceT getSource()
    {
        return this.source;
    }

    public boolean isSource(Class<? extends CommandSource> type)
    {
        return type.isAssignableFrom(this.source.getClass());
    }
}
