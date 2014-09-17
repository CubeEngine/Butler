package de.cubeisland.engine.command;

import java.util.Arrays;

/**
 * String Utility Methods
 */
public class StringUtils
{
    /**
     * Joins the given Strings together using the delimiter
     *
     * @param delimiter the delimiter
     * @param strings the strings to join
     * @return the joined String
     */
    public static String join(String delimiter, Iterable<String> strings)
    {
        String del = "";
        StringBuilder sb = new StringBuilder();
        for (String token : strings) // excluding current (unconsumed) token
        {
            sb.append(del).append(token);
            del = delimiter;
        }
        return sb.toString();
    }

    /**
     * Joins the given Strings together using the delimiter
     *
     * @param delimiter the delimiter
     * @param strings the strings to join
     * @return the joined String
     */
    public static String join(String delimiter, String[] strings)
    {
        return join(delimiter, Arrays.asList(strings));
    }
}
