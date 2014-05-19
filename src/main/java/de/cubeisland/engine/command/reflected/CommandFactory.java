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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.BaseCommand;
import de.cubeisland.engine.command.BaseCommandContext;
import de.cubeisland.engine.command.CommandHolder;
import de.cubeisland.engine.command.CommandManager;
import de.cubeisland.engine.command.CommandOwner;
import de.cubeisland.engine.command.PermissionProvider;
import de.cubeisland.engine.command.ReflectedCommandDescriptor;
import de.cubeisland.engine.command.exception.InvalidSignatureException;
import de.cubeisland.engine.command.reflected.annotation.Alias;
import de.cubeisland.engine.command.reflected.annotation.Command;
import de.cubeisland.engine.command.reflected.annotation.Flags;
import de.cubeisland.engine.command.reflected.annotation.IndexedParams;
import de.cubeisland.engine.command.reflected.annotation.NamedParams;
import de.cubeisland.engine.command.reflected.annotation.Permission;
import de.cubeisland.engine.command.reflected.annotation.RestrictUsage;

public class CommandFactory
{
    private final PermissionProvider permissionProvider;

    public CommandFactory(PermissionProvider permissionProvider)
    {
        this.permissionProvider = permissionProvider;
    }

    public List<BaseCommand> parseCommands(CommandManager manager, CommandOwner owner, Object holder)
    {
        // TODO Unloggable
        // TODO AsyncCall
        List<BaseCommand> commands = new ArrayList<BaseCommand>();
        BaseCommand parent = null;
        if (holder instanceof CommandHolder)
        {
            for (Constructor<?> aHolder : holder.getClass().getConstructors())
            {
                if (aHolder.isAnnotationPresent(Command.class))
                {
                    Command aCommand = aHolder.getAnnotation(Command.class);
                    Permission aPermission = aHolder.getAnnotation(Permission.class);
                    Alias aAlias = aHolder.getAnnotation(Alias.class);
                    RestrictUsage aRUsage = aHolder.getAnnotation(RestrictUsage.class);
                    IndexedParams aIndexed = aHolder.getAnnotation(IndexedParams.class);
                    NamedParams aNamed = aHolder.getAnnotation(NamedParams.class);
                    Flags aFlags = aHolder.getAnnotation(Flags.class);
                    ReflectedCommandDescriptor descriptor = this.newDescriptor();

                    descriptor.newCommand(aCommand, aPermission, holder.getClass().getSimpleName(), permissionProvider);
                    descriptor.reflect(holder, null).owner(owner).alias(aAlias).restrict(aRUsage);
                    descriptor.context(aIndexed, aNamed, aFlags);

                    parent = descriptor.toCommand(manager);
                    commands.add(parent);
                    break;
                }
            }
        }

        for (Method aHolder : holder.getClass().getDeclaredMethods())
        {
            if (Modifier.isStatic(aHolder.getModifiers()))
            {
                continue;
            }
            if (aHolder.isAnnotationPresent(Command.class))
            {
                Class<?>[] methodParams = aHolder.getParameterTypes();
                if (methodParams.length != 1 || !BaseCommandContext.class.isAssignableFrom(methodParams[0]))
                {
                    throw new InvalidSignatureException(holder, aHolder);
                }

                Command aCommand = aHolder.getAnnotation(Command.class);
                Permission aPermission = aHolder.getAnnotation(Permission.class);
                Alias aAlias = aHolder.getAnnotation(Alias.class);
                RestrictUsage aRUsage = aHolder.getAnnotation(RestrictUsage.class);
                IndexedParams aIndexed = aHolder.getAnnotation(IndexedParams.class);
                NamedParams aNamed = aHolder.getAnnotation(NamedParams.class);
                Flags aFlags = aHolder.getAnnotation(Flags.class);
                ReflectedCommandDescriptor descriptor = newDescriptor();

                descriptor.newCommand(aCommand, aPermission, holder.getClass().getSimpleName(), permissionProvider);
                descriptor.reflect(holder, aHolder).owner(owner).alias(aAlias).restrict(aRUsage);
                descriptor.context(aIndexed, aNamed, aFlags).parent(parent);

                commands.add(descriptor.toCommand(manager));
            }
        }
        return commands;
    }

    public ReflectedCommandDescriptor newDescriptor()
    {
        return new ReflectedCommandDescriptor();
    }
}
