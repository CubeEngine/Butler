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
package de.cubeisland.engine.command.methodic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.cubeisland.engine.command.CommandBuilder;
import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.ImmutableCommandDescriptor;
import de.cubeisland.engine.command.Name;
import de.cubeisland.engine.command.UsageProvider;
import de.cubeisland.engine.command.alias.Alias;
import de.cubeisland.engine.command.alias.AliasConfiguration;
import de.cubeisland.engine.command.alias.Aliases;
import de.cubeisland.engine.command.methodic.context.BaseCommandContext;
import de.cubeisland.engine.command.parameter.FlagParameter;
import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.parameter.ParameterGroup;
import de.cubeisland.engine.command.parameter.ParameterUsageGenerator;
import de.cubeisland.engine.command.parameter.SimpleParameter;
import de.cubeisland.engine.command.parameter.property.Description;
import de.cubeisland.engine.command.parameter.property.FixedPosition;
import de.cubeisland.engine.command.parameter.property.FixedValues;
import de.cubeisland.engine.command.parameter.property.Greed;
import de.cubeisland.engine.command.parameter.property.Required;
import de.cubeisland.engine.command.parameter.property.ValueLabel;

// TODO BaseAlias + Alias pre/suffix arguments
// AliasCommand
public class MethodicBuilder<OriginT extends InvokableMethod> implements CommandBuilder<BasicMethodicCommand, OriginT>
{
    protected ParameterUsageGenerator usageGenerator = new ParameterUsageGenerator();

    @Override
    public BasicMethodicCommand buildCommand(OriginT origin)
    {
        Method method = origin.getMethod();
        return this.isApplicable(method) ? this.build(method.getAnnotation(Command.class), origin) : null;
    }

    /**
     * Checks whether the method can be built into a Command by this builder
     *
     * @param method the method to check
     *
     * @return true if a command can be built from this method
     */
    protected boolean isApplicable(Method method)
    {
        if (method.isAnnotationPresent(Command.class))
        {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1 && BaseCommandContext.class.isAssignableFrom(parameterTypes[0]))
            {
                return true;
            }
        }
        return false;
    }

    protected BasicMethodicCommand build(Command annotation, OriginT origin)
    {
        ImmutableCommandDescriptor descriptor = buildCommandDescriptor(annotation, origin);
        descriptor.setProperty(buildParameters(descriptor, origin));
        return new BasicMethodicCommand(descriptor);
    }

    protected ImmutableCommandDescriptor buildCommandDescriptor(Command annotation, OriginT origin)
    {
        ImmutableCommandDescriptor descriptor = new ImmutableCommandDescriptor();
        descriptor.setProperty(new Name(annotation.name().isEmpty() ? origin.getMethod().getName() : annotation.name()));
        descriptor.setProperty(new Description(annotation.desc()));
        descriptor.setProperty(new InvokableMethodProperty(origin.getMethod(), origin.getHolder()));
        descriptor.setProperty(new UsageProvider(usageGenerator));

        List<AliasConfiguration> aliasList = new ArrayList<>();
        for (String name : annotation.alias())
        {
            aliasList.add(new AliasConfiguration(name));
        }
        Alias alias = origin.getMethod().getAnnotation(Alias.class);
        if (alias != null)
        {
            for (String name : alias.names())
            {
                AliasConfiguration aliasConf = new AliasConfiguration(name, alias.parents());
                aliasConf.setPrefix(alias.prefix());
                aliasConf.setSuffix(alias.suffix());
                aliasList.add(aliasConf);
            }
        }
        descriptor.setProperty(new Aliases(aliasList));

        return descriptor;
    }

    protected ParameterGroup buildParameters(CommandDescriptor descriptor, OriginT origin)
    {
        Method method = origin.getMethod();
        Flags flags = method.getAnnotation(Flags.class);
        List<Parameter> flagsList = new ArrayList<>();
        if (flags != null)
        {
            for (Flag flag : flags.value())
            {
                flagsList.add(new FlagParameter(flag.name(), flag.longName()));
            }
        }
        Params params = method.getAnnotation(Params.class);
        List<Parameter> nPosList = new ArrayList<>();
        List<Parameter> posList = new ArrayList<>();
        if (params != null)
        {
            for (Param param : params.nonpositional())
            {
                nPosList.add(this.createParameter(param, origin));
            }
            for (Param param : params.positional())
            {
                Parameter parameter = this.createParameter(param, origin);
                // TODO set valuelabel if not set but "needed"
                parameter.setProperty(new FixedPosition(posList.size()));
                posList.add(parameter);
            }
        }
        return new ParameterGroup(flagsList, nPosList, posList);
    }

    private Parameter createParameter(Param param, OriginT origin)
    {
        Class type = param.type();
        Class reader = param.reader();
        if (reader == Void.class)
        {
            reader = type;
            if (Enum.class.isAssignableFrom(type))
            {
                reader = Enum.class;
            }
        }

        SimpleParameter parameter = new SimpleParameter(type, reader);
        int greed = param.greed();
        parameter.setProperty(new Greed(greed));
        String label = param.label();
        if (!label.isEmpty())
        {
            parameter.setProperty(new ValueLabel(label));
        }
        String[] names = param.names();
        if (names.length != 0)
        {
            parameter.setProperty(new FixedValues(names));
        }

        parameter.setProperty(param.req() ? Required.REQUIRED : Required.OPTIONAL);

        String desc = param.desc();
        if (!desc.isEmpty())
        {
            parameter.setProperty(new Description(desc));
        }

        // TODO completer
        return parameter;
    }

    public static Set<Method> getMethods(Class holder)
    {
        HashSet<Method> methods = new LinkedHashSet<>();
        methods.addAll(Arrays.asList(holder.getMethods()));
        for (Method method : methods)
        {
            method.setAccessible(true);
        }
        methods.addAll(Arrays.asList(holder.getDeclaredMethods()));
        return methods;
    }
}
