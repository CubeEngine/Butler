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
package de.cubeisland.engine.old.command.conversation;

import java.util.HashSet;
import java.util.Set;

import de.cubeisland.engine.command.BaseCommand;
import de.cubeisland.engine.command.CommandManager;
import de.cubeisland.engine.command.CommandOwner;

public abstract class ConversationCommand extends BaseCommand
{
    private final Set<Long> usersInMode = new HashSet<Long>();

    protected ConversationCommand(CommandManager manager, CommandOwner owner, ConversationContextFactory contextFactory)
    {

        super(manager, owner, "", "", contextFactory, null);
        // TODO owner.getCore().getEventManager().registerListener(owner, this);
    }

    /*
    public boolean hasUser(User user)
    {
        return usersInMode.contains(user.getId());
    }

    @EventHandler
    public void onChatHandler(AsyncPlayerChatEvent event)
    {
        User user = this.getOwner().getCore().getUserManager().getExactUser(event.getPlayer().getUniqueId());
        if (this.hasUser(user))
        {
            user.sendMessage(
                ChatFormat.PURPLE + "[" + ChatFormat.WHITE + "ChatCommand" + ChatFormat.PURPLE + "] " + ChatFormat.WHITE
                    + event.getMessage()
                            );
            Stack<String> labels = new Stack<>();
            labels.push(this.getLabel());
            CommandContext context = this.getContextFactory().parse(this, user, labels, StringUtils.explode(" ",
                                                                                                            event.getMessage()));
            this.run(context);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTabComplete(PlayerChatTabCompleteEvent event)
    {
        User user = this.getOwner().getCore().getUserManager().getExactUser(event.getPlayer().getUniqueId());
        if (this.hasUser(user))
        {
            event.getTabCompletions().clear();

            Stack<String> labels = new Stack<>();
            labels.push(this.getLabel());
            CommandContext context = this.getContextFactory().tabCompleteParse(this, user, labels, StringUtils.explode(
                " ", event.getChatMessage()));
            event.getTabCompletions().addAll(this.tabComplete(context));
        }
    }

    @Override
    public List<String> tabComplete(ParameterizedTabContext context)
    {
        List<String> list = new ArrayList<>();
        Set<String> flags = new HashSet<>();
        Set<String> params = new HashSet<>();
        for (CommandFlag flag : this.getContextFactory().getFlags())
        {
            flags.add(flag.getLongName().toLowerCase());
        }
        for (CommandParameter param : this.getContextFactory().getParameters())
        {
            params.add(param.getName().toLowerCase());
        }
        List<Object> args = context.getIndexed();
        if (args.isEmpty())
        {
            list.addAll(flags);
            list.addAll(params);
        }
        else
        {
            final int argc = args.size();
            String lastArg = args.get(argc - 1).toString().toLowerCase();
            String beforeLastArg = argc - 2 >= 0 ? args.get(argc - 2).toString() : null;
            if (lastArg.isEmpty())
            {
                //check for named
                if (beforeLastArg != null && params.contains(beforeLastArg.toLowerCase()))
                {
                    return this.getContextFactory().getParameter(beforeLastArg).getCompleter().complete(context,
                                                                                                        lastArg);
                }
                else
                {
                    list.addAll(flags);
                    list.addAll(params);
                }
            }
            else
            {
                //check for named
                if (beforeLastArg != null && params.contains(beforeLastArg.toLowerCase()))
                {
                    return this.getContextFactory().getParameter(beforeLastArg).getCompleter().complete(context,
                                                                                                        lastArg);
                }
                else // check starting
                {
                    for (String flag : flags)
                    {
                        if (flag.startsWith(lastArg))
                        {
                            list.add(flag);
                        }
                    }
                    for (String param : params)
                    {
                        if (param.startsWith(lastArg))
                        {
                            list.add(param);
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public ConversationContextFactory getContextFactory()
    {
        return (ConversationContextFactory)super.getContextFactory();
    }

    public boolean addUser(User user)
    {
        return this.usersInMode.add(user.getId());
    }

    public void removeUser(User user)
    {
        this.usersInMode.remove(user.getId());
    }

    @Override
    public void help(HelpContext context)
    {
        context.sendTranslated(NEUTRAL, "Flags:");
        Set<String> flags = new HashSet<>();
        for (CommandFlag flag : this.getContextFactory().getFlags())
        {
            flags.add(flag.getLongName().toLowerCase());
        }
        context.sendMessage("    " + StringUtils.implode(ChatFormat.GREY + ", " + ChatFormat.WHITE, flags));
        context.sendTranslated(NEUTRAL, "Parameters:");
        Set<String> params = new HashSet<>();
        for (CommandParameter param : this.getContextFactory().getParameters())
        {
            params.add(param.getName().toLowerCase());
        }
        context.sendMessage("    " + StringUtils.implode(ChatFormat.GREY + ", " + ChatFormat.WHITE, params));
    }

    */
}
