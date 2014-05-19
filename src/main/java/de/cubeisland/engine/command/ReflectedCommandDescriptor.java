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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import de.cubeisland.engine.command.completer.Completer;
import de.cubeisland.engine.command.completer.IndexedParameterCompleter;
import de.cubeisland.engine.command.reflected.ReflectedCommand;
import de.cubeisland.engine.command.reflected.annotation.Alias;
import de.cubeisland.engine.command.reflected.annotation.Command;
import de.cubeisland.engine.command.reflected.annotation.Flag;
import de.cubeisland.engine.command.reflected.annotation.Flags;
import de.cubeisland.engine.command.reflected.annotation.Grouped;
import de.cubeisland.engine.command.reflected.annotation.Indexed;
import de.cubeisland.engine.command.reflected.annotation.IndexedParams;
import de.cubeisland.engine.command.reflected.annotation.NamedParams;
import de.cubeisland.engine.command.reflected.annotation.Param;
import de.cubeisland.engine.command.reflected.annotation.Permission;
import de.cubeisland.engine.command.reflected.annotation.RestrictUsage;

public class ReflectedCommandDescriptor implements CommandDescriptor
{
    private PermissionProvider permProvider;

    private String name;
    private String description;
    private String[] alias;

    @Override
    public String[] getAlias()
    {
        return alias;
    }

    private boolean checkperm;
    private CommandPermission permission;
    private ContextFactory contextFactory;
    private Object holder;
    private Method method;
    private CommandOwner owner;
    private Class<? extends BaseCommandSender>[] restrictUsage;
    private BaseCommand parent;

    private DelegatingContextFilter delegation;

    public ReflectedCommandDescriptor newCommand(Command command, Permission permission, String defaultName,
                                                 PermissionProvider permissionProvider)
    {
        this.permProvider = permissionProvider;
        this.name = command.name();
        if (this.name.isEmpty())
        {
            this.name = defaultName;
        }
        this.description = command.desc();
        this.checkperm = command.checkPerm();

        this.permission = permissionProvider == null ? null : permissionProvider.getPermission(permission, defaultName,
                                                                                               checkperm);
        return this;
    }

    public ReflectedCommandDescriptor context(IndexedParams iParam, NamedParams nParam, Flags flags)
    {
        this.contextFactory = new ContextFactory();

        if (iParam != null)
        {
            int index = 0;
            for (Grouped arg : iParam.value())
            {
                Indexed[] indexed = arg.value();
                if (indexed.length == 0)
                {
                    throw new IllegalArgumentException("You have to define at least one Indexed!");
                }
                Indexed aIndexed = indexed[0];
                String[] labels = aIndexed.label();
                if (labels.length == 0)
                {
                    labels = new String[]{String.valueOf(index)};
                }

                int greed = indexed.length;
                if (arg.greedy())
                {
                    greed = -1;
                }
                CommandParameterIndexed indexedParam = new CommandParameterIndexed(labels, aIndexed.type(), arg.req(),
                                                                                   aIndexed.req(), greed,
                                                                                   null); // TODO perm
                indexedParam.setCompleter(generateCompleter(aIndexed.completer()));

                Set<String> staticLabels = new HashSet<String>();
                for (String label : labels)
                {
                    if (label.startsWith("!"))
                    {
                        staticLabels.add(label.substring(1));
                    }
                }

                if (!staticLabels.isEmpty())
                {
                    indexedParam.setCompleter(new IndexedParameterCompleter(indexedParam.getCompleter(), staticLabels));
                }

                contextFactory.addIndexed(indexedParam);

                if (indexed.length > 1)
                {
                    for (int i = 1; i < indexed.length; i++)
                    {
                        index++;
                        aIndexed = indexed[i];
                        labels = aIndexed.label();
                        if (labels.length == 0)
                        {
                            labels = new String[]{String.valueOf(index)};
                        }
                        indexedParam = new CommandParameterIndexed(labels, aIndexed.type(), arg.req(), aIndexed.req(),
                                                                   0, null); // TODO perm
                        indexedParam.setCompleter(generateCompleter(aIndexed.completer()));
                        contextFactory.addIndexed(indexedParam);
                    }
                }
                index++;
            }
        }

        if (nParam != null)
        {
            for (Param param : nParam.value())
            {
                contextFactory.addParameter(this.readParameter(param, permission));
            }
        }
        if (flags != null)
        {
            for (Flag flag : flags.value())
            {
                contextFactory.addFlag(new CommandFlag(flag.name(), flag.longName(), this.permProvider
                                                                                         == null ? null : this.permProvider.getPermission(
                    flag.perm(), permission)));
            }
        }

        return this;
    }

    private CommandParameter readParameter(Param param, CommandPermission basePerm)
    {
        // TODO multivalue param
        // TODO greedy param (take args until next keyword)
        final CommandParameter cParam = new CommandParameter(param.name(), param.label(), param.type(),
                                                             this.permProvider
                                                                 == null ? null : this.permProvider.getPermission(
                                                                 param.permission(), basePerm));
        cParam.addAliases(param.aliases());
        cParam.setRequired(param.required());
        cParam.setCompleter(generateCompleter(param.completer()));

        return cParam;
    }

    private Completer generateCompleter(Class<? extends Completer> completerClass)
    {
        if (completerClass == Completer.class)
        {
            return null;
        }
        try
        {
            return completerClass.newInstance();
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Failed to create the completer " + completerClass.getName(), ex);
        }
    }

    public ReflectedCommandDescriptor reflect(Object holder, Method method)
    {
        this.holder = holder;
        this.method = method;
        return this;
    }

    public ReflectedCommandDescriptor alias(Alias alias)
    {
        if (alias != null)
        {
            this.alias = alias.names();
        }
        return this;
    }

    public ReflectedCommandDescriptor owner(CommandOwner owner)
    {
        this.owner = owner;
        return this;
    }

    public ReflectedCommandDescriptor restrict(RestrictUsage annotation)
    {
        if (annotation != null)
        {
            this.restrictUsage = annotation.value();
        }
        return this;
    }

    public BaseCommand toCommand(CommandManager manager)
    {
        return new ReflectedCommand(manager, this);
    }

    public ReflectedCommandDescriptor delegate(DelegatingContextFilter delegation)
    {
        this.delegation = delegation;
        return this;
    }

    @Override
    public DelegatingContextFilter getDelegation()
    {
        return this.delegation;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public CommandPermission getPermission()
    {
        return permission;
    }

    @Override
    public ContextFactory getContextFactory()
    {
        return contextFactory;
    }

    public Object getHolder()
    {
        return holder;
    }

    public Method getMethod()
    {
        return method;
    }

    @Override
    public CommandOwner getOwner()
    {
        return owner;
    }

    @Override
    public Class<? extends BaseCommandSender>[] getRestrictUsage()
    {
        return restrictUsage;
    }

    @Override
    public BaseCommand getParent()
    {
        return parent;
    }

    public ReflectedCommandDescriptor parent(BaseCommand parent)
    {
        this.parent = parent;
        return this;
    }

    // TODO loggable
    // TODO asnyc
}
