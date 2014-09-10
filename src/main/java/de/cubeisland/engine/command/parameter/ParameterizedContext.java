package de.cubeisland.engine.command.parameter;

import java.util.List;

import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.ContextDescriptor;

public class ParameterizedContext<CallT extends CommandCall> extends CommandContext<CallT>
{
    public ParameterizedContext(CallT call, String[] rawArgs, List<String> parentCalls, ContextDescriptor descriptor)
    {
        super(call, rawArgs, parentCalls);
        ParsedParameter parsed = descriptor.parse(rawArgs, 0);
    }

    private void parse(String[] tokens, ContextDescriptor descriptor)
    {

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
