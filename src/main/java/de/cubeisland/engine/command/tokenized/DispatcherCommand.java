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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.engine.command.CommandBase;
import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.CommandDispatcher;
import de.cubeisland.engine.command.StringUtils;

/**
 * A Command that can dispatch sub-commands
 */
public class DispatcherCommand implements CommandDispatcher<TokenizedInvocation>
{
    private final CommandDescriptor descriptor;

    private final Map<String, CommandBase<TokenizedInvocation>> commands = new HashMap<>();

    public DispatcherCommand(CommandDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    protected DispatcherCommand()
    {
        this.descriptor = this.selfDescribe();
    }

    protected CommandDescriptor selfDescribe()
    {
        return this.descriptor;
    }

    @Override
    public void registerCommand(CommandBase<TokenizedInvocation> command)
    {
        CommandDescriptor descriptor = command.getDescriptor();
        CommandBase replaced = this.commands.put(descriptor.getName().toLowerCase(), command);
        if (replaced != null)
        {
            // TODO replacement Policy
            // for now always replace
        }
        for (String alias : descriptor.getAliases())
        {
            replaced = this.commands.put(alias.toLowerCase(), command);
            if (replaced != null)
            {
                // TODO replacement Policy
                // for now always replace
            }
        }
    }


    @Override
    public Set<CommandBase<TokenizedInvocation>> getCommands()
    {
        return Collections.unmodifiableSet(new HashSet<>(this.commands.values()));
    }

    @Override
    public boolean hasCommand(String alias)
    {
        return this.commands.containsKey(alias.toLowerCase());
    }

    @Override
    public CommandBase<TokenizedInvocation> getCommand(String alias)
    {
        return this.commands.get(alias.toLowerCase());
    }

    @Override
    public boolean run(TokenizedInvocation call)
    {
        if (!call.tokens().isEmpty())
        {
            CommandBase<TokenizedInvocation> command = this.getCommand(call.currentToken());
            if (command != null)
            {
                return command.run(call.subCall());
            }
        }
        return this.handleNotFound(call);
    }

    protected boolean handleNotFound(TokenizedInvocation call)
    {
        // TODO override in CE to get Help subcmd when empty args OR cmd not found to show possible sub-cmds (did you mean... ?) OR delegation
        return false;
    }

    @Override
    public List<String> getSuggestions(TokenizedInvocation call)
    {
        List<String> tokens = call.tokens();
        List<String> result = new ArrayList<>();
        if (tokens.isEmpty())
        {
            for (CommandBase command : this.getCommands())
            {
                result.add(command.getDescriptor().getName());
            }
        }
        else if (tokens.size() == 1)
        {
            String curToken = call.currentToken().toLowerCase();
            for (String alias : this.commands.keySet())
            {
                if (alias.startsWith(curToken))
                {
                    result.add(StringUtils.join(" ", call.getParentCalls()) + " " + alias);
                }
            }
        }
        else
        {
            String curToken = call.currentToken();
            CommandBase<TokenizedInvocation> command = this.getCommand(curToken);
            if (command == null)
            {
                return result; // Nothing to tab
            }
            return command.getSuggestions(call.subCall());
        }
        return result;
    }

    @Override
    public CommandDescriptor getDescriptor()
    {
        return this.descriptor;
    }
}
