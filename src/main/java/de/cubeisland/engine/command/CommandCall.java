package de.cubeisland.engine.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * a CommandCall
 */
public class CommandCall
{
    private final String[] tokens;
    private final Map<Object, Object> callArguments = new HashMap<>();

    public CommandCall(String args)
    {
        this.tokens = args.split(" ");
    }

    private CommandCall(String[] tokens)
    {
        this.tokens = tokens;
    }

    /**
     * Checks if the CommandCall has a value
     *
     * @param value the value to find
     *
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
     *
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
     *
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
     *
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
     *
     * @return the value or null if not found or not of expected Type
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> expectedType)
    {
        Object o = this.get(key);
        if (expectedType.isAssignableFrom(o.getClass()))
        {
            return (T)o;
        }
        return null;
    }

    /**
     * Returns the value for the specified key
     *
     * @param key the key to find a value for
     * @param <T> the Type of the returned value
     *
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

    public CommandCall subCall()
    {
        CommandCall commandCall = new CommandCall(Arrays.copyOfRange(this.tokens, 1, this.tokens.length - 1));
        commandCall.callArguments.putAll(this.callArguments);
        return commandCall;
    }
}
