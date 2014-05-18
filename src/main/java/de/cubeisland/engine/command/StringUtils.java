package de.cubeisland.engine.command;

public class StringUtils
{
    public static boolean startsWithIgnoreCase(String string, String token)
    {
        if (string.length() < token.length())
        {
            return false;
        }
        return string.substring(0, token.length()).equalsIgnoreCase(token);
    }

    public static String implode(String delim, Iterable<String> toImplode)
    {
        String del = "";
        StringBuilder sb = new StringBuilder();
        for (String name : toImplode)
        {
            sb.append(del).append(name);
            del = delim;
        }
        return sb.toString();
    }
}
