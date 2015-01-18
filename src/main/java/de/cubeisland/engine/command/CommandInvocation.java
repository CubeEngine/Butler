/**
 * The MIT License
 * Copyright (c) 2014 Cube Island
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
package de.cubeisland.engine.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import de.cubeisland.engine.command.parameter.reader.ReaderManager;
import de.cubeisland.engine.command.util.property.PropertyHolder;

import static de.cubeisland.engine.command.StringUtils.join;

/**
 * The invocation of a command
 * Its tokens can be consumed on after the other.
 * Quoted Strings are parsed as one token
 */
public class CommandInvocation extends PropertyHolder
{
    public static final String SPACE = " ";
    private final CommandSource commandSource;
    private final String commandLine;

    private final LinkedHashMap<String, CommandBase> invocationPath = new LinkedHashMap<>();

    private final List<String> tokens;
    private final ReaderManager manager;

    private int consumed = 0;

    public CommandInvocation(CommandSource source, String commandLine, String delim, ReaderManager manager)
    {
        this.commandSource = source;
        this.commandLine = commandLine;
        this.tokens = tokenize(commandLine, delim);
        this.manager = manager;
    }

    public CommandInvocation(CommandSource source, String commandLine, ReaderManager manager)
    {
        this(source, commandLine, SPACE, manager);
    }

    protected List<String> tokenize(String commandLine, String delim)
    {
        String[] rawTokens = commandLine.split(delim, -1);
        List<String> stringParsed = new ArrayList<>();
        for (int offset = 0; offset < rawTokens.length; )
        {
            StringBuilder sb = new StringBuilder();
            offset += StringUtils.parseString(sb, rawTokens, offset);
            stringParsed.add(sb.toString());
        }
        return stringParsed;
    }

    /**
     * Returns the String the Call got created with
     *
     * @return the CallString
     */
    public List<String> tokens()
    {
        return tokens;
    }

    /**
     * Returns the ReaderManager
     *
     * @return the readerManager
     */
    public ReaderManager getManager()
    {
        return manager;
    }

    /**
     * Returns the Locale to be used for this CommandCall
     *
     * @return the locale
     */
    public Locale getLocale()
    {
        return commandSource.getLocale();
    }

    /**
     * Consumes given amount of tokens
     *
     * @param amount the amount of tokens to consume
     *
     * @return the first consumed token
     */
    public String consume(int amount)
    {
        String token = this.currentToken();
        this.consumed += amount;
        return token;
    }

    /**
     * Resets the consumed parameter counter to given value
     *
     * @param consumed the value
     */
    public void reset(int consumed)
    {
        this.consumed = consumed;
    }


    /**
     * Returns the amount of tokens consumed
     *
     * @return the amount of tokens consumed
     */
    public int consumed()
    {
        return consumed;
    }

    /**
     * Returns the current token
     *
     * @return the current token
     */
    public String currentToken()
    {
        return this.tokens.get(this.consumed);
    }

    /**
     * Returns the token at given index
     *
     * @param index the index
     *
     * @return the token at given index
     */
    public String tokenAt(int index)
    {
        return this.tokens.get(index);
    }

    /**
     * Returns whether all tokens are consumed
     *
     * @return true if all tokens are consumed
     */
    public boolean isConsumed()
    {
        return this.consumed >= this.tokens.size();
    }

    /**
     * Returns the consumed tokens joined together since
     *
     * @param start the index of the token to start with
     *
     * @return the joined tokens
     */
    public String tokensSince(int start)
    {

        return join(" ", this.tokens.subList(start, this.consumed));
    }

    /**
     * Returns the commandline this Call was initialized with
     *
     * @return the commandline
     */
    public String getCommandLine()
    {
        return this.commandLine;
    }

    /**
     * Returns the CommandSource that initialized this Call
     *
     * @return the CommandSource
     */
    public CommandSource getCommandSource()
    {
        return this.commandSource;
    }

    public CommandInvocation subInvocation(CommandBase command)
    {
        this.invocationPath.put(this.consume(1), command);
        return this;
    }

    /**
     * Returns a list of the commands higher up in the call chain
     *
     * @return the labels
     */
    public List<String> getLabels()
    {
        return new ArrayList<>(this.invocationPath.keySet());
    }

    /**
     * Gets a copy of this Invocation with other tokens
     *
     * @param tokens    the tokens
     * @param delimiter the delimiter
     *
     * @return the new invocation
     */
    public CommandInvocation setTokens(String tokens, String delimiter)
    {
        CommandInvocation invocation = new CommandInvocation(this.getCommandSource(), tokens, delimiter,
                                                             this.getManager());
        invocation.properties.putAll(this.properties);
        return invocation;
    }
}
