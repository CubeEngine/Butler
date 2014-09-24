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
package de.cubeisland.engine.command.tokenized;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.StringUtils;
import de.cubeisland.engine.command.property.PropertyHolder;
import de.cubeisland.engine.command.parameter.reader.ReaderManager;

import static de.cubeisland.engine.command.StringUtils.join;

/**
 * A tokenized Invocation of a command.
 * Its tokens can be consumed on after the other.
 * Quoted Strings are parsed as one token
 */
public class TokenizedInvocation extends PropertyHolder implements CommandInvocation
{
    private final CommandSource commandSource;
    private final String commandLine;
    private final List<String> parentCalls = new ArrayList<>();

    private final List<String> tokens;
    private final ReaderManager manager;

    private int consumed = 0;

    public TokenizedInvocation(CommandSource source, String commandLine, String delim, ReaderManager manager)
    {
        this.commandSource = source;
        this.commandLine = commandLine;
        String[] rawTokens = commandLine.split(delim);

        List<String> stringParsed = new ArrayList<>();
        for (int offset = 0; offset < rawTokens.length; )
        {
            StringBuilder sb = new StringBuilder();
            offset += StringUtils.parseString(sb, rawTokens, offset);
            stringParsed.add(sb.toString());
        }
        this.tokens = stringParsed;
        this.manager = manager;
    }

    public TokenizedInvocation(CommandSource source, String commandLine, ReaderManager manager)
    {
        this(source, commandLine, " ", manager);
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
     */
    public void consume(int amount)
    {
        this.consumed += amount;
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

    @Override
    public String getCommandLine()
    {
        return this.commandLine;
    }

    @Override
    public CommandSource getCommandSource()
    {
        return this.commandSource;
    }

    public TokenizedInvocation subCall()
    {
        this.parentCalls.add(this.currentToken());
        this.consume(1);
        return this;
    }

    @Override
    public List<String> getParentCalls()
    {
        return Collections.unmodifiableList(this.parentCalls);
    }
}
