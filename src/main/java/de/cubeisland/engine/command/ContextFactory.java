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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import static de.cubeisland.engine.command.Type.*;
import static java.util.Locale.ENGLISH;

public class ContextFactory
{
    private final LinkedHashMap<Integer, CommandParameterIndexed> indexed = new LinkedHashMap<Integer, CommandParameterIndexed>();
    private final Map<String, CommandParameter> named = new LinkedHashMap<String, CommandParameter>();
    private final Map<String, CommandFlag> flags = new LinkedHashMap<String, CommandFlag>();
    private ArgBounds bounds;
    private int indexedCount = 0;

    protected static int readString(StringBuilder sb, String[] args, int offset)
    {
        if (offset >= args.length || args[offset].isEmpty())
        {
            // string is empty? return an empty string
            sb.append("");
            return 1;
        }

        // first char is not a quote char? return the string
        final char quoteChar = args[offset].charAt(0);
        if (quoteChar != '"' && quoteChar != '\'')
        {
            sb.append(args[offset]);
            return 1;
        }

        String string = args[offset].substring(1);
        // string has at least 2 chars and ends with the same quote char? return the string without quotes
        if (string.length() > 0 && string.charAt(string.length() - 1) == quoteChar)
        {
            sb.append(string.substring(0, string.length() - 1));
            return 1;
        }

        sb.append(string);
        offset++;
        int argCounter = 1;

        while (offset < args.length)
        {
            sb.append(' ');
            argCounter++;
            string = args[offset++];
            if (string.length() > 0 && string.charAt(string.length() - 1) == quoteChar)
            {
                sb.append(string.substring(0, string.length() - 1));
                break;
            }
            sb.append(string);
        }
        return argCounter;
    }

    public ArgBounds getArgBounds()
    {
        return this.bounds;
    }

    public final ContextFactory addIndexed(CommandParameterIndexed indexed)
    {
        this.indexed.put(indexedCount++, indexed);
        return this;
    }

    public final ContextFactory removeLastIndexed()
    {
        this.indexed.remove(--indexedCount);
        return this;
    }

    public final CommandParameterIndexed getIndexed(int index)
    {
        return this.indexed.get(index);
    }

    public List<CommandParameterIndexed> getIndexedParameters()
    {
        return new ArrayList<CommandParameterIndexed>(this.indexed.values());
    }

    public ContextFactory addParameters(Collection<CommandParameter> params)
    {
        if (params != null)
        {
            for (CommandParameter param : params)
            {
                this.addParameter(param);
            }
        }
        return this;
    }

    public ContextFactory addParameter(CommandParameter param)
    {
        this.named.put(param.getName().toLowerCase(ENGLISH), param);
        for (String alias : param.getAliases())
        {
            alias = alias.toLowerCase(ENGLISH);
            if (!this.named.containsKey(alias))
            {
                this.named.put(alias, param);
            }
        }
        return this;
    }

    public ContextFactory removeParameter(String name)
    {
        CommandParameter param = this.named.remove(name.toLowerCase(ENGLISH));
        if (param != null)
        {
            Iterator<Entry<String, CommandParameter>> it = this.named.entrySet().iterator();
            while (it.hasNext())
            {
                if (it.next().getValue() == param)
                {
                    it.remove();
                }
            }
        }
        return this;
    }

    public CommandParameter getParameter(String name)
    {
        return this.named.get(name.toLowerCase(ENGLISH));
    }

    public LinkedHashMap<String, CommandParameter> getParameters()
    {
        return new LinkedHashMap<String, CommandParameter>(this.named);
    }

    public ContextFactory addFlag(CommandFlag flag)
    {
        this.flags.put(flag.getName().toLowerCase(ENGLISH), flag);
        final String longName = flag.getLongName().toLowerCase(ENGLISH);
        if (!this.flags.containsKey(longName))
        {
            this.flags.put(longName, flag);
        }
        return this;
    }

    public ContextFactory removeFlag(String name)
    {
        CommandFlag flag = this.flags.remove(name.toLowerCase(ENGLISH));
        if (flag != null)
        {
            Iterator<Map.Entry<String, CommandFlag>> it = this.flags.entrySet().iterator();
            while (it.hasNext())
            {
                if (it.next().getValue() == flag)
                {
                    it.remove();
                }
            }
        }
        return this;
    }

    public CommandFlag getFlag(String name)
    {
        return this.flags.get(name.toLowerCase(ENGLISH));
    }

    public Set<CommandFlag> getFlags()
    {
        return new HashSet<CommandFlag>(this.flags.values());
    }

    public void calculateArgBounds()
    {
        this.bounds = new ArgBounds(new ArrayList<CommandParameterIndexed>(this.indexed.values()));
    }

    public CommandContext parse(BaseCommand command, BaseCommandSender sender, Stack<String> labels, String[] rawArgs)
    {
        List<String> indexed = new LinkedList<String>();
        Set<String> flags = new HashSet<String>();
        Map<String, String> named = new LinkedHashMap<String, String>();
        return new CommandContext(command, sender, labels, indexed, flags, named, readCommand(rawArgs, flags, indexed,
                                                                                              named));
    }

    private Type readCommand(String[] rawArgs, Set<String> flags, List<String> args, Map<String, String> rawParams)
    {
        if (rawArgs.length < 1)
        {
            return Type.NOTHING;
        }
        LastType type = new LastType();
        for (int offset = 0; offset < rawArgs.length; )
        {
            String rawArg = rawArgs[offset];
            if (rawArg.isEmpty())
            {
                // ignore empty args except last when tabcomplete
                if (offset == rawArgs.length - 1)
                {
                    args.add(rawArg);
                }
                offset++;
                type.last = ANY;
            }
            else if (rawArg.length() >= 1 && rawArg.charAt(0) == '-')
            {
                // reads a flag or indexed param
                offset = readFlag(rawArg, args, flags, offset, type);
            }
            else
            {
                // reads a named param or indexed param
                offset = readRawParam(rawArgs, args, rawParams, offset, type);
            }
        }

        return type.last;
    }

    private int readFlag(String rawArg, List<String> args, Set<String> flags, int offset, LastType type)
    {
        String flag = rawArg;
        if (flag.charAt(0) == '-')
        {
            flag = flag.substring(1);
        }
        if (flag.isEmpty()) // is there still a name?
        {
            offset++;
            args.add(rawArg);
            type.last = FLAG_OR_INDEXED;
            return offset;
        }

        flag = flag.toLowerCase(ENGLISH); // lowercase flag

        CommandFlag cmdFlag = this.flags.get(flag);
        if (cmdFlag != null) // has flag ?
        {
            flags.add(cmdFlag.getName()); // added flag
            type.last = NOTHING;
        }
        else
        {
            type.last = FLAG_OR_INDEXED;
            args.add(rawArg); // flag not found, adding it as an indexed param
        }
        offset++;
        return offset;
    }

    private int readRawParam(String[] rawArgs, List<String> args, Map<String, String> rawParams, int offset,
                             LastType type)
    {
        String paramName = rawArgs[offset].toLowerCase(ENGLISH);
        // has alias named Param ?
        CommandParameter param = named.get(paramName);
        // is named Param?
        if (param != null && offset + 1 < rawArgs.length)
        {
            StringBuilder paramValue = new StringBuilder();
            offset++;
            offset += readString(paramValue, rawArgs, offset);
            //added named param
            rawParams.put(param.getName(), paramValue.toString());
            type.last = PARAM_VALUE;
        }
        else // else is indexed param
        {
            StringBuilder arg = new StringBuilder();
            offset += readString(arg, rawArgs, offset);
            args.add(arg.toString());// added indexed param
            type.last = INDEXED_OR_PARAM;
        }
        return offset;
    }
}
