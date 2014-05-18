package de.cubeisland.engine.command.completer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.CommandFlag;
import de.cubeisland.engine.command.CommandParameter;
import de.cubeisland.engine.command.CommandParameterIndexed;
import de.cubeisland.engine.command.ContextFactory;
import de.cubeisland.engine.command.StringUtils;
import de.cubeisland.engine.command.Type;

import static de.cubeisland.engine.command.Type.*;

public class CompleterUtils
{

    public static List<String> tabComplete(CommandContext context, Type lastType)
    {
        if (lastType == NOTHING)
        {
            return null;
        }
        final ContextFactory cFactory = context.getCommand().getContextFactory();
        if (lastType == PARAM_VALUE)
        {
            return tabCompleteParamValue(context, cFactory);
        }
        List<String> result = new ArrayList<>();
        List<Object> args = context.getIndexed();
        String lastArg = args.get(args.size() - 1).toString();
        if (lastType == FLAG_OR_INDEXED)
        {
            tabCompleteFlags(context, cFactory, result, lastArg);
            tabCompleteIndexed(context, cFactory, result, args.size() - 1, lastArg);
        }
        else if (lastType == INDEXED_OR_PARAM)
        {
            tabCompleteIndexed(context, cFactory, result, args.size() - 1, lastArg);
            tabCompleteParam(context, cFactory, result, lastArg);
        }
        else if (lastType == ANY)
        {
            tabCompleteIndexed(context, cFactory, result, args.size() - 1, lastArg);
            tabCompleteParam(context, cFactory, result, lastArg);
            tabCompleteFlags(context, cFactory, result, lastArg);
        }
        if (result.isEmpty())
        {
            return null; //TODO remove once ALL our commands have tabcompleter for players
        }
        return result;
    }

    private static List<String> tabCompleteParamValue(CommandContext context, ContextFactory cFactory)
    {
        Iterator<Entry<String, String>> iterator = context.getRawNamed().entrySet().iterator();
        Entry<String, String> last;
        do
        {
            last = iterator.next();
        }
        while (iterator.hasNext());
        Completer completer = cFactory.getParameter(last.getKey()).getCompleter();
        if (completer != null)
        {
            return completer.complete(context, last.getValue());
        }
        return null; //TODO remove once ALL our commands have tabcompleter for players
    }

    private static void tabCompleteParam(CommandContext context, ContextFactory cFactory, List<String> result,
                                         String last)
    {
        for (CommandParameter parameter : cFactory.getParameters())
        {
            if (!context.hasParam(parameter.getName()))
            {
                if (StringUtils.startsWithIgnoreCase(parameter.getName(), last))
                {
                    result.add(parameter.getName());
                }
                if (!last.isEmpty())
                {
                    for (String alias : parameter.getAliases())
                    {
                        if (alias.length() > 2 && StringUtils.startsWithIgnoreCase(alias, last))
                        {
                            result.add(alias);
                        }
                    }
                }
            }
        }
    }

    private static void tabCompleteIndexed(CommandContext context, ContextFactory cFactory, List<String> result,
                                           int index, String last)
    {
        CommandParameterIndexed indexed = cFactory.getIndexed(index);
        if (indexed != null)
        {
            Completer indexedCompleter = indexed.getCompleter();
            if (indexedCompleter != null)
            {
                result.addAll(indexedCompleter.complete(context, last));
            }
        }
    }

    private static void tabCompleteFlags(CommandContext context, ContextFactory cFactory, List<String> result,
                                         String last)
    {
        if (!last.isEmpty())
        {
            last = last.substring(1);
        }
        for (CommandFlag commandFlag : cFactory.getFlags())
        {
            if (!context.hasFlag(commandFlag.getName()) && StringUtils.startsWithIgnoreCase(commandFlag.getLongName(),
                                                                                            last))
            {
                result.add("-" + commandFlag.getLongName());
            }
        }
    }
}
