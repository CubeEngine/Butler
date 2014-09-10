package de.cubeisland.engine.command;

import de.cubeisland.engine.command.parameter.reader.ReaderManager;

import java.util.*;

/**
 * a CommandCall
 */
public class CommandCall
{
    private final String[] tokens;
    private final Map<Object, Object> callArguments = new HashMap<>();
    private final ReaderManager manager;
    private final Locale locale;

    /**
     * Creates a CommandCall from a commandline
     *
     * @param args the commandline
     * @param locale
     */
    public CommandCall(String args, ReaderManager manager, Locale locale)
    {
        String[] rawTokens = args.split(" ");

        List<String> stringParsed = new ArrayList<>();
        for (int offset = 0; offset < rawTokens.length; offset++)
        {
            StringBuilder sb = new StringBuilder();
            offset += parseString(sb, rawTokens, offset);
            stringParsed.add(sb.toString());
        }
        stringParsed.toArray(new String[stringParsed.size()])
        this.tokens = args;
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

    /**
     * Checks if the CommandCall has a value
     *
     * @param value the value to find
     * @return true if the value is found
     */
    public boolean containsValue(Object value)
    {
        return callArguments.containsValue(value);
    }

    /**
     * Checks if the CommandCall has a value for the key
     *
     * @param key the key to find a value for
     * @return true if the key has a value
     */
    public boolean containsKey(Object key)
    {
        return callArguments.containsKey(key);
    }

    /**
     * Returns the value for the specified key
     *
     * @param key the key to find a value for
     * @return the value or {@code null} if not found
     */
    public Object get(Object key)
    {
        return callArguments.get(key);
    }

    /**
     * Sets the value for the specified key
     *
     * @param key   the key to assign the value to
     * @param value the value
     * @return the previous value for given key
     */
    public Object put(Object key, Object value)
    {

        return callArguments.put(key, value);
    }

    /**
     * Returns the value for the specified key
     *
     * @param key          the key to find a value for
     * @param expectedType the expected returnType
     * @param <T>          the Type of the returned value
     * @return the value or null if not found or not of expected Type
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> expectedType)
    {
        Object o = this.get(key);
        if (expectedType.isAssignableFrom(o.getClass()))
        {
            return (T) o;
        }
        return null;
    }

    /**
     * Returns the value for the specified key
     *
     * @param key the key to find a value for
     * @param <T> the Type of the returned value
     * @return the value or null if no found or not of expected Type
     */
    public <T> T get(Class<T> key)
    {
        return this.get(key, key);
    }

    /**
     * Returns the String the Call got created with
     *
     * @return the CallString
     */
    public String[] getTokens()
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
        CommandCall commandCall = new CommandCall(Arrays.copyOfRange(this.tokens, 1, this.tokens.length - 1), this.manager);
        commandCall.callArguments.putAll(this.callArguments);
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

    public Locale getLocale()
    {
        return locale;
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
}
