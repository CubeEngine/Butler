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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import de.cubeisland.engine.command.completer.CompleterUtils;
import de.cubeisland.engine.command.exception.CommandException;
import de.cubeisland.engine.command.exception.MissingParameterException;
import de.cubeisland.engine.command.exception.PermissionDeniedException;
import de.cubeisland.engine.command.exception.TooFewArgumentsException;
import de.cubeisland.engine.command.exception.TooManyArgumentsException;
import de.cubeisland.engine.command.exception.UsageRestrictedException;

import static de.cubeisland.engine.command.StringUtils.implode;


public abstract class BaseCommand
{
    private final BaseCommand parent;
    private final Map<String, BaseCommand> children = new HashMap<String, BaseCommand>();
    private final Map<String, AliasCommand> aliases = new HashMap<String, AliasCommand>();
    private final Stack<String> labels;
    private final String name;
    private final String description;
    private final CommandOwner owner;
    private final ContextFactory contextFactory;
    private final Class<? extends BaseCommandSender>[] restrictUsage;
    private final CommandPermission permission;
    private boolean registered = false;

    private DelegatingContextFilter delegation;

    private final CommandManager commandManager;

    protected BaseCommand(CommandManager manager, CommandDescriptor descriptor)
    {
        this.commandManager = manager;
        this.name = descriptor.getName();
        this.description = descriptor.getDescription();
        this.owner = descriptor.getOwner();
        this.contextFactory = descriptor.getContextFactory();
        this.restrictUsage = descriptor.getRestrictUsage();
        this.permission = descriptor.getPermission();
        this.parent = descriptor.getParent();

        this.delegation = descriptor.getDelegation();

        Stack<String> labels = new Stack<String>();
        BaseCommand cmd = this;
        do
        {
            labels.push(cmd.getName());
        }
        while ((cmd = cmd.getParent()) != null);
        this.labels = labels;
    }

    public final Stack<String> getLabels()
    {
        Stack<String> stack = new Stack<String>();
        stack.addAll(labels);
        return stack;
    }

    public CommandOwner getOwner()
    {
        return owner;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    protected void addAlias(String name, BaseCommand... parents) // TODO prefix / suffix arguments
    {
        if (parents.length == 0)
        {
            this.addAlias(new AliasCommand(name, this, null));
        }
        else
        {
            for (BaseCommand parent : parents)
            {
                parent.addAlias(new AliasCommand(name, this, parent));
            }
        }
    }

    private void addAlias(AliasCommand alias)
    {
        this.aliases.put(alias.getName(), alias);
    }

    public boolean isRestricted(BaseCommandSender sender)
    {
        if (this.restrictUsage != null)
        {
            for (Class<? extends BaseCommandSender> clazz : this.restrictUsage)
            {
                if (clazz.isAssignableFrom(sender.getClass()))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public ContextFactory getContextFactory()
    {
        return this.contextFactory;
    }

    public BaseCommand getParent()
    {
        return parent;
    }

    public boolean isRegistered()
    {
        return registered;
    }

    public void setRegistered()
    {
        this.registered = true;
    }

    public final String getUsage()
    {
        return "/" + this.getUsage(this.getLabels(), Locale.getDefault(), null);
    }

    public final String getUsage(Locale locale, BaseCommandSender sender)
    {
        return this.getUsage0(locale, sender);
    }

    public String getUsage(BaseCommandSender sender)
    {
        return this.getUsage(this.getLabels(), sender.getLocale(), sender);
    }

    public String getUsage(BaseCommandContext ctx)
    {
        return this.getUsage(ctx.getLabels(), ctx.getSender().getLocale(), ctx.getSender());
    }

    private String getUsage(Stack<String> labels, Locale locale, Permissible permissible)
    {
        return implode(" ", labels) + " " + this.getUsage0(locale, permissible);
    }

    protected String getUsage0(Locale locale, Permissible sender)
    {
        // TODO indexed permissions
        StringBuilder sb = new StringBuilder();
        int inGroup = 0;
        for (CommandParameterIndexed indexedParam : this.getContextFactory().getIndexedParameters())
        {
            if (indexedParam.getCount() == 1 || indexedParam.getCount() < 0)
            {
                sb.append(convertLabel(indexedParam.isGroupRequired(), implode("|", convertLabels(indexedParam))));
                sb.append(' ');
                inGroup = 0;
            }
            else if (indexedParam.getCount() > 1)
            {
                sb.append(indexedParam.isGroupRequired() ? '<' : '[');
                sb.append(convertLabel(indexedParam.isRequired(), implode("|", convertLabels(indexedParam))));
                sb.append(' ');
                inGroup = indexedParam.getCount() - 1;
            }
            else if (indexedParam.getCount() == 0)
            {
                sb.append(convertLabel(indexedParam.isRequired(), implode("|", convertLabels(indexedParam))));
                inGroup--;
                if (inGroup == 0)
                {
                    sb.append(indexedParam.isGroupRequired() ? '>' : ']');
                }
                sb.append(' ');
            }
        }

        for (CommandParameter param : this.getContextFactory().getParameters().values())
        {
            if (param.checkPermission(sender))
            {
                if (param.isRequired())
                {
                    sb.append('<').append(param.getName()).append(" <").append(param.getLabel()).append(">> ");
                }
                else
                {
                    sb.append('[').append(param.getName()).append(" <").append(param.getLabel()).append(">] ");
                }
            }
        }
        for (CommandFlag flag : this.getContextFactory().getFlags())
        {
            if (flag.checkPermission(sender))
            {
                sb.append("[-").append(flag.getLongName()).append("] ");
            }
        }
        return sb.toString().trim();
    }

    private List<String> convertLabels(CommandParameterIndexed indexedParam)
    {
        String[] labels = indexedParam.getLabels().clone();
        String[] rawLabels = indexedParam.getLabels();
        for (int i = 0; i < rawLabels.length; i++)
        {
            if (rawLabels.length == 1)
            {
                labels[i] = convertLabel(true, "!" + rawLabels[i]);
            }
            else
            {
                labels[i] = convertLabel(true, rawLabels[i]);
            }
        }
        return Arrays.asList(labels);
    }

    private String convertLabel(boolean req, String label)
    {
        if (label.startsWith("!"))
        {
            return label.substring(1);
        }
        else if (req)
        {
            return "<" + label + ">";
        }
        else
        {
            return "[" + label + "]";
        }
    }



    public final BaseCommand getChild(String name)
    {
        if (name == null)
        {
            return null;
        }
        return this.children.get(name.toLowerCase(Locale.ENGLISH));
    }

    public final Set<BaseCommand> getChildren()
    {
        return new HashSet<BaseCommand>(children.values());
    }

    public final boolean hasChildren()
    {
        return !this.children.isEmpty();
    }

    public final boolean hasChild(String name)
    {
        return name != null && this.children.containsKey(name.toLowerCase(Locale.ENGLISH));
    }

    /**
     * This method handles the command execution
     *
     * @param context The CommandContext containing all the necessary information
     */
    public abstract CommandResult run(BaseCommandContext context);

    public void checkContext(BaseCommandContext ctx) throws CommandException
    {
        BaseCommand command = ctx.getCommand();
        ContextFactory cFactory = command.getContextFactory();
        if (command.isRestricted(ctx.getSender()))
        {
            throw new UsageRestrictedException();
        }
        if (!command.isAuthorized(ctx.getSender()))
        {
            throw new PermissionDeniedException(command.permission);
        }
        ArgBounds bounds = cFactory.getArgBounds();
        if (ctx.getIndexedCount() < bounds.getMin())
        {
            throw new TooFewArgumentsException();
        }
        if (bounds.getMax() > ArgBounds.NO_MAX && ctx.getIndexedCount() > bounds.getMax())
        {
            throw new TooManyArgumentsException();
        }
        for (CommandParameterIndexed indexed : cFactory.getIndexedParameters())
        {
            // TODO permission for indexed
        }
        for (CommandParameter param : cFactory.getParameters().values())
        {
            if (ctx.hasParam(param.getName()))
            {
                if (!param.checkPermission(ctx.getSender()))
                {
                    throw new PermissionDeniedException(param.getPermission());
                }
            }
            else if (param.isRequired())
            {
                throw new MissingParameterException(param.getName());
            }
        }
        for (CommandFlag flag : cFactory.getFlags())
        {
            if (ctx.hasFlag(flag.getName()) && !flag.checkPermission(ctx.getSender()))
            {
                throw new PermissionDeniedException(flag.getPermission());
            }
        }
    }

    public List<String> tabComplete(BaseCommandContext context)
    {
        return CompleterUtils.tabComplete(context, context.last);
    }

    public abstract void help(BaseCommandContext ctx);

    public boolean isAuthorized(BaseCommandSender sender)
    {
        return this.permission.isAuthorized(sender);
    }

    public CommandPermission getPermission()
    {
        return this.permission;
    }

    Class<? extends BaseCommandSender>[] getRestrictUsage()
    {
        return this.restrictUsage;
    }

    public DelegatingContextFilter getDelegation()
    {
        return delegation;
    }

    public void delegate(DelegatingContextFilter delegation)
    {
        this.delegation = delegation;
    }

    public CommandManager getCommandManager()
    {
        return this.commandManager;
    }

    public ArrayList<AliasCommand> getAliases()
    {
        return new ArrayList<AliasCommand>(aliases.values());
    }
}
