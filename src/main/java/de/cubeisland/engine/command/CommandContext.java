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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import de.cubeisland.engine.command.reader.ArgumentReader;
import de.cubeisland.engine.old.command.exception.InvalidArgumentException;
import de.cubeisland.engine.old.command.exception.PermissionDeniedException;

import static java.util.Locale.ENGLISH;

public class CommandContext
{
    final Type last;
    private final BaseCommand command;
    private final BaseCommandSender sender;
    private final Stack<String> labels;
    private final List<String> rawIndexed;
    private final Set<String> flags;
    private final Map<String, String> rawNamed;
    private List<Object> indexed = null;
    private Map<String, Object> named = null;

    public CommandContext(BaseCommand command, BaseCommandSender sender, Stack<String> labels, List<String> rawIndexed,
                          Set<String> flags, Map<String, String> rawNamed, Type last)
    {
        this.command = command;
        this.sender = sender;
        this.labels = labels;
        this.rawIndexed = rawIndexed;
        this.flags = flags;
        this.rawNamed = rawNamed;
        this.last = last;
    }

    /**
     * Returns the cube command that was executed
     *
     * @return the command
     */
    public BaseCommand getCommand()
    {
        return this.command;
    }

    /**
     * Checks whether the command sender is compatible with the given class
     *
     * @param type the class to check against
     *
     * @return true is the command sender is assignment compatible with the given class
     */
    public boolean isSender(Class<? extends BaseCommandSender> type)
    {
        return type.isAssignableFrom(this.sender.getClass());
    }

    /**
     * Returns the CommandSender
     *
     * @return the command sender
     */
    public BaseCommandSender getSender()
    {
        return this.sender;
    }

    /**
     * Returns the label that was used to run this command
     *
     * @return the label
     */
    public String getLabel()
    {
        return this.labels.peek();
    }

    /**
     * Returns all labels that have been used to execute this command
     *
     * @return a stack of labels
     */
    public Stack<String> getLabels()
    {
        Stack<String> newStack = new Stack<>();
        newStack.addAll(this.labels);
        return newStack;
    }

    /**
     * This method is a proxy to {@link BaseCommandSender#sendTranslated}
     */
    public void sendTranslated(String message, Object... args)
    {
        this.sender.sendTranslated(message, args);
    }

    /**
     * This method is a proxy to {@link BaseCommandSender#sendTranslatedN}
     */
    public void sendTranslatedN(int count, String sMessage, String pMessage, Object... args)
    {
        this.sender.sendTranslatedN(count, sMessage, pMessage, args);
    }

    public void sendMessage(String msg)
    {
        this.sender.sendMessage(msg);
    }

    /**
     * Returns the amount of indexed parameters
     */
    public int getIndexedCount()
    {
        return this.rawIndexed.size();
    }

    /**
     * Returns a list of the indexed parameters
     */
    public List<Object> getIndexed()
    {
        return new ArrayList<>(this.indexed);
    }

    /**
     * Checks whether the given index is available in the indexed list
     *
     * @param i the index to check
     */
    public boolean hasArg(int i)
    {
        return i >= 0 && i < getIndexedCount();
    }

    /**
     * The method returns a arg as a specific type
     *
     * @param index the index
     * @param def   the default value
     */
    public <T> T getIndexed(int index, T def)
    {
        try
        {
            T value = this.getIndexe(index);
            if (value != null)
            {
                return value;
            }
        }
        catch (ClassCastException ignored)
        {
        }
        return def;
    }

    /**
     * * The method returns a arg as a specific type
     *
     * @param index the index
     *
     * @return the converted arg value
     */
    @SuppressWarnings("unchecked")
    public <T> T getIndexe(int index)
    {
        try
        {
            return (T)this.indexed.get(index);
        }
        catch (IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    /**
     * This method aggregates all string from the given index
     *
     * @param from the index to start from
     *
     * @return the aggregated string
     */
    public String getStrings(int from)
    {
        if (!this.hasArg(from))
        {
            return null;
        }
        StringBuilder sb = new StringBuilder(this.<String>getIndexe(from));
        while (this.hasArg(++from))
        {
            sb.append(" ").append(this.getIndexe(from));
        }
        return sb.toString();
    }

    public boolean hasFlag(String name)
    {
        return this.flags.contains(name.toLowerCase(ENGLISH));
    }

    public boolean hasFlags(String... names)
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

    public Set<String> getFlags()
    {
        return new HashSet<>(this.flags);
    }

    public LinkedHashMap<String, Object> getParams()
    {
        return new LinkedHashMap<>(this.named);
    }

    public boolean hasParam(String name)
    {
        return this.rawNamed.containsKey(name.toLowerCase(ENGLISH));
    }

    @SuppressWarnings("unchecked")
    public <T> T getParam(String name)
    {
        return (T)this.named.get(name.toLowerCase(ENGLISH));
    }

    public <T> T getParam(String name, T def)
    {
        try
        {
            T value = this.getParam(name);
            if (value != null)
            {
                return value;
            }
        }
        catch (ClassCastException ignored)
        {
        }
        return def;
    }

    public String getString(String name)
    {
        return this.rawNamed.get(name);
    }

    public String getString(String name, String def)
    {
        try
        {
            String string = this.getString(name);
            if (string != null)
            {
                return string;
            }
        }
        catch (IndexOutOfBoundsException ignored)
        {
        }
        return def;
    }

    public String getString(int index)
    {
        return this.rawIndexed.get(index);
    }

    public String getString(int index, String def)
    {
        try
        {
            String string = this.getString(index);
            if (string != null)
            {
                return string;
            }
        }
        catch (IndexOutOfBoundsException ignored)
        {
        }
        return def;
    }


    public void ensurePermission(CommandPermission permission) throws PermissionDeniedException
    {
        if (!permission.isAuthorized(this.getSender()))
        {
            throw new PermissionDeniedException(permission);
        }
    }

    private List<Object> readIndexed(BaseCommandSender sender)
    {
        List<CommandParameterIndexed> iParams = this.command.getContextFactory().getIndexedParameters();
        List<Object> result = new ArrayList<>();
        int i = 0;
        if (rawIndexed.get(rawIndexed.size() - 1).isEmpty())
        {
            rawIndexed.remove(rawIndexed.size() - 1);
        }
        for (String rInd : rawIndexed)
        {
            CommandParameterIndexed pIndexed = iParams.get(i++);
            InvalidArgumentException e = null;
            for (Class<?> type : pIndexed.getType())
            {
                try
                {
                    result.add(ArgumentReader.read(type, rInd, sender));
                    e = null;
                    break;
                }
                catch (InvalidArgumentException ex)
                {
                    e = ex;
                }
            }
            if (e != null)
            {
                e.setPosition(i);
                throw e;
            }
        }
        return result;
    }


    private Map<String, Object> readNamed(BaseCommandSender sender)
    {
        LinkedHashMap<String, CommandParameter> nParams = this.command.getContextFactory().getParameters();
        Map<String, Object> readParams = new LinkedHashMap<>();

        for (Entry<String, String> entry : rawNamed.entrySet())
        {
            CommandParameter param = nParams.get(entry.getKey());
            try
            {
                readParams.put(entry.getKey(), ArgumentReader.read(param.getType(), entry.getValue(), sender));
            }
            catch (InvalidArgumentException ex)
            {
                ex.setPosition(param.getName());
                throw ex;
            }
        }
        return readParams;
    }

    public Map<String, String> getRawNamed()
    {
        return rawNamed;
    }

    public List<String> getRawIndexed()
    {
        return this.rawIndexed;
    }


    protected void runAndShowResult()
    {
        this.indexed = this.readIndexed(this.sender);
        this.named = this.readNamed(this.sender);

        this.command.checkContext(this);
        CommandResult result = this.command.run(this);
        if (result != null)
        {
            result.show(this);
        }
    }
}
