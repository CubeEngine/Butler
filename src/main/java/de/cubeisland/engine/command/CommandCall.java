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

import de.cubeisland.engine.command.parameter.reader.ReaderManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static de.cubeisland.engine.command.StringUtils.join;

/**
 * a CommandCall
 */
public class CommandCall extends PropertyHolder
{
    private final String[] tokens;
    private final ReaderManager manager;
    private final Locale locale;
    private int consumed = 0;

    /**
     * Creates a CommandCall from a commandline
     *
     * @param args   the commandline
     * @param locale the locale
     */
    public CommandCall(String args, ReaderManager manager, Locale locale)
    {
        String[] rawTokens = args.split(" ");

        List<String> stringParsed = new ArrayList<>();
        for (int offset = 0; offset < rawTokens.length; )
        {
            StringBuilder sb = new StringBuilder();
            offset += parseString(sb, rawTokens, offset);
            stringParsed.add(sb.toString());
        }
        this.tokens = stringParsed.toArray(new String[stringParsed.size()]);
        this.manager = manager;
        this.locale = locale;
    }

    /**
     * Internal constructor to get the subordinated call
     *
     * @param tokens the remaining tokens
     */
    private CommandCall(String[] tokens, ReaderManager manager, Locale locale)
    {
        this.tokens = tokens;
        this.manager = manager;
        this.locale = locale;
    }

    public static int parseString(StringBuilder sb, String[] args, int offset)
    {
        // string is empty? return an empty string
        if (offset >= args.length || args[offset].isEmpty())
        {
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
        int consumed = 1;

        while (offset < args.length)
        {
            sb.append(' ');
            consumed++;
            string = args[offset++];
            if (string.length() > 0 && string.charAt(string.length() - 1) == quoteChar)
            {
                sb.append(string.substring(0, string.length() - 1));
                break;
            }
            sb.append(string);
        }

        return consumed;
    }

    /**
     * Returns the String the Call got created with
     *
     * @return the CallString
     */
    public String[] tokens()
    {
        return tokens;
    }

    /**
     * Creates the subordinated CommandCall
     *
     * @return the subordinated CommandCall
     */
    public CommandCall subCall()
    {
        CommandCall commandCall = new CommandCall(Arrays.copyOfRange(this.tokens, 1, this.tokens.length - 1),
                this.manager, this.locale);
        commandCall.properties.putAll(this.properties);
        return commandCall;
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
        return locale;
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
        return this.tokens[this.consumed];
    }

    /**
     * Creates a CommandCall inheriting from this one with a new set of unconsumed tokens
     * which can be used to pass a token that is a list down to another Reader
     *
     * @param tokens the tokens
     * @return the new CommandCall
     */
    public CommandCall subTokens(String[] tokens)
    {
        CommandCall commandCall = new CommandCall(tokens, this.manager, this.locale);
        commandCall.properties.putAll(this.properties);
        return commandCall;
    }

    /**
     * Returns whether all tokens are consumed
     *
     * @return true if all tokens are consumed
     */
    public boolean isConsumed()
    {
        return this.consumed >= this.tokens.length;
    }

    /**
     * Returns the consumed tokens joined together since
     *
     * @param start the index of the token to start with
     * @return the joined tokens
     */
    public String tokensSince(int start)
    {
        return join(" ", Arrays.copyOfRange(this.tokens, start, this.consumed));
    }
}
