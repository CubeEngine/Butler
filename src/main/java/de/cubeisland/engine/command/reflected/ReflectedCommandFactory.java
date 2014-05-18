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
package de.cubeisland.engine.command.reflected;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.CommandFactory;
import de.cubeisland.engine.command.CommandFlag;
import de.cubeisland.engine.command.CommandManager;
import de.cubeisland.engine.command.CommandOwner;
import de.cubeisland.engine.command.CommandParameter;
import de.cubeisland.engine.command.CommandParameterIndexed;
import de.cubeisland.engine.command.CommandPermission;
import de.cubeisland.engine.command.CommandPermission.PermDefault;
import de.cubeisland.engine.command.ContextFactory;
import de.cubeisland.engine.command.completer.Completer;
import de.cubeisland.engine.command.completer.IndexedParameterCompleter;

public abstract class ReflectedCommandFactory<T extends ReflectedCommand> implements CommandFactory<T>
{
    @SuppressWarnings("unchecked")
    public Class<T> getCommandType()
    {
        return (Class<T>)ReflectedCommand.class;
    }

    protected void validateSignature(Object holder, Method method)
    {
        Class<?>[] methodParams = method.getParameterTypes();
        if (methodParams.length != 1 || !CommandContext.class.isAssignableFrom(methodParams[0]))
        {
            // TODO throw Exception instead
            //module.getLog().warn("The method ''{}.{}'' does not match the required method signature: public void {}(CommandContext context)",holder.getClass().getSimpleName(), method.getName(), method.getName());
            throw new IllegalArgumentException();
        }
    }

    protected abstract CommandPermission get(CommandPermission parent, String name, byte permDefault);
    // TODO permission impl: CommandPermission.detachedPermission(permNode, permDefault);
    // return null for empty name

    @SuppressWarnings("unchecked")
    protected T buildCommand(CommandManager manager, CommandOwner owner, Object holder, Method method, Command cmdAnnot)
    {
        String[] commandNames = cmdAnnot.names();
        if (commandNames.length == 0)
        {
            commandNames = new String[]{
                method.getName()
            };
        }

        String name = commandNames[0].trim().toLowerCase(Locale.ENGLISH);
        Set<String> aliases = new HashSet<>(commandNames.length - 1);
        for (int i = 1; i < commandNames.length; ++i)
        {
            aliases.add(commandNames[i].toLowerCase(Locale.ENGLISH));
        }

        String permNode = name;
        byte permDefault = PermDefault.OP;
        if (method.isAnnotationPresent(Permission.class))
        {
            Permission permAnnot = method.getAnnotation(Permission.class);
            if (!permAnnot.value().isEmpty())
            {
                permNode = permAnnot.value();
            }
            permDefault = permAnnot.permDefault();
            if (permAnnot.checkPerm())
            {
                permDefault = PermDefault.TRUE;
            }
        }
        CommandPermission permission = this.get(null, permNode, permDefault);

        ContextFactory factory = this.createContextFactory();

        if (method.isAnnotationPresent(Flags.class))
        {
            for (Flag flag : method.getAnnotation(Flags.class).value())
            {
                CommandPermission flagPerm = null;
                if (!flag.permission().isEmpty())
                {
                    flagPerm = this.get(permission, flag.permission(), flag.permDefault());
                }
                factory.addFlag(new CommandFlag(flag.name(), flag.longName(), flagPerm));
            }
        }

        if (method.isAnnotationPresent(NamedParams.class))
        {
            for (Param param : method.getAnnotation(NamedParams.class).value())
            {
                // TODO multivalue param
                // TODO greedy param (take args until next keyword)

                String[] names = param.names();
                if (names.length < 1)
                {
                    continue;
                }
                String[] paramAliases;
                if (names.length > 1)
                {
                    paramAliases = Arrays.copyOfRange(names, 1, names.length);
                }
                else
                {
                    paramAliases = new String[0];
                }

                CommandPermission paramPerm = null;
                if (!param.permission().isEmpty())
                {
                    paramPerm = this.get(permission, param.permission(), param.permDefault());
                }
                final CommandParameter cParam = new CommandParameter(names[0], param.label(), param.type(), paramPerm);
                cParam.addAliases(paramAliases);
                cParam.setRequired(param.required());
                cParam.setCompleter(getCompleter(param.completer()));
                factory.addParameter(cParam);
            }
        }

        if (method.isAnnotationPresent(IndexedParams.class))
        {
            int index = 0;
            for (Grouped arg : method.getAnnotation(IndexedParams.class).value())
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
                                                                                   aIndexed.req(), greed);
                indexedParam.setCompleter(getCompleter(aIndexed.completer()));

                Set<String> staticLabels = new HashSet<>();
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

                factory.addIndexed(indexedParam);

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
                                                                   0);
                        indexedParam.setCompleter(getCompleter(aIndexed.completer()));
                        factory.addIndexed(indexedParam);
                    }
                }
                index++;
            }
        }

        if (method.isAnnotationPresent(Alias.class))
        {
            /* // TODO
            Alias annotation = method.getAnnotation(Alias.class);
            if (annotation != null)
            {
                this.registerAlias(annotation.names(), annotation.parents(), annotation.prefix(), annotation.suffix());
            }
            */
        }

        ReflectedCommand cmd = new ReflectedCommand(manager, owner, holder, method, name, cmdAnnot.desc(), factory,
                                                    permission);
        // TODO cmd.setLoggable(cmdAnnot.loggable());
        // TODO cmd.setAsynchronous(cmdAnnot.async());
        if (method.isAnnotationPresent(RestrictUsage.class))
        {
            cmd.restrictUsage(method.getAnnotation(RestrictUsage.class).value());
        }

        return (T)cmd;
    }

    private Completer getCompleter(Class<? extends Completer> completerClass)
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
            // TODO module.getLog().error(ex, "Failed to create the completer '{}'", completerClass.getName());
            throw new IllegalStateException();
        }
    }

    protected ContextFactory createContextFactory()
    {
        return new ContextFactory();
    }

    @Override
    public List<T> parseCommands(CommandManager manager, CommandOwner owner, Object holder)
    {
        List<T> commands = new ArrayList<>();

        for (Method method : holder.getClass().getDeclaredMethods())
        {
            if (Modifier.isStatic(method.getModifiers()))
            {
                continue;
            }

            Command annotation = method.getAnnotation(Command.class);
            if (annotation == null)
            {
                continue;
            }
            this.validateSignature(holder, method);
            T command = this.buildCommand(manager, owner, holder, method, annotation);
            if (command != null)
            {
                commands.add(command);
            }
        }

        return commands;
    }
}
