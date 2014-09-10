package de.cubeisland.engine.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Command that can dispatch sub-commands
 */
public class DispatcherCommand implements CommandDispatcher
{
    private final Map<String, CommandBase> commands = new HashMap<>();
    private final CommandDescriptor descriptor;

    public DispatcherCommand(CommandDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    @Override
    public void registerCommand(CommandBase command)
    {
        // TODO in CE autoregister child ? for Help
        CommandDescriptor descriptor = command.getDescriptor();
        CommandBase replaced = this.commands.put(descriptor.getName().toLowerCase(), command);
        if (replaced != null)
        {
            // TODO replacedment Policy
            // for now always replace
        }
        for (String alias : descriptor.getAliases())
        {
            replaced = this.commands.put(alias.toLowerCase(), command);
            if (replaced != null)
            {
                // TODO replacedment Policy
                // for now always replace
            }
        }
    }

    @Override
    public Set<CommandBase> getCommands()
    {
        return Collections.unmodifiableSet(new HashSet<>(this.commands.values()));
    }

    @Override
    public boolean contains(String alias)
    {
        return this.commands.containsKey(alias.toLowerCase());
    }

    @Override
    public CommandBase get(String alias)
    {
        return this.commands.get(alias.toLowerCase());
    }

    @Override
    public boolean run(CommandCall call, List<String> parentCalls)
    {
        // TODO override in CE to get Help subcmd when empty args OR cmd not found to show possible sub-cmds (did you mean... ?) OR delegation
        String[] tokens = call.getTokens();
        if (tokens.length == 0)
        {
            return false;
        }
        CommandBase command = this.get(tokens[0]);
        if (command == null)
        {
            return false;
        }
        List<String> newParentCalls = new ArrayList<>(parentCalls);
        newParentCalls.add(tokens[0]);
        return command.run(call.subCall(), newParentCalls);
    }

    @Override
    public List<String> getSuggestions(CommandCall call, List<String> previousTokens)
    {
        String[] tokens = call.getTokens();
        List<String> result = new ArrayList<>();
        if (tokens.length == 0)
        {
            for (CommandBase command : this.getCommands())
            {
                result.add(command.getDescriptor().getName());
            }
        }
        else if (tokens.length == 1)
        {
            for (String alias : this.commands.keySet())
            {
                if (alias.startsWith(tokens[0].toLowerCase()))
                {
                    result.add(String.join(" ", previousTokens) + " " + alias);
                }
            }
        }
        else
        {
            CommandBase command = this.get(tokens[0]);
            if (command == null)
            {
                return result; // Nothing to tab
            }
            ArrayList<String> newParentCalls = new ArrayList<>(previousTokens);
            newParentCalls.add(tokens[0]);
            return command.getSuggestions(call.subCall(), newParentCalls);
        }
        return result;
    }

    @Override
    public CommandDescriptor getDescriptor()
    {
        return this.descriptor;
    }
}
