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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.Command;
import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.DispatcherCommand;
import de.cubeisland.engine.command.SimpleCommandDescriptor;
import de.cubeisland.engine.command.parameter.FlagParameter;
import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.parameter.ParameterGroup;
import de.cubeisland.engine.command.parameter.ParameterizedContext;
import de.cubeisland.engine.command.parameter.ParsedParameters;
import de.cubeisland.engine.command.parameter.SimpleParameter;
import de.cubeisland.engine.command.parameter.property.Description;
import de.cubeisland.engine.command.parameter.property.FixedPosition;
import de.cubeisland.engine.command.parameter.property.FixedValues;
import de.cubeisland.engine.command.parameter.property.Greed;
import de.cubeisland.engine.command.parameter.property.Required;
import de.cubeisland.engine.command.parameter.property.ValueLabel;
import de.cubeisland.engine.command.parameter.property.ValueReader;
import de.cubeisland.engine.command.parameter.property.ValueType;

public class MethodicCommand extends DispatcherCommand
{
    protected final Object holder;
    protected final Method method;
    protected final ParameterGroup ctxDescriptor;

    public MethodicCommand(Object holder, Method method)
    {
        super(buildDescriptor(method));
        this.holder = holder;
        this.method = method;
        this.ctxDescriptor = createContextDescriptor(method);
    }

    private static CommandDescriptor buildDescriptor(Method method)
    {
        Command cmdAnnot = method.getAnnotation(Command.class);
        String name = cmdAnnot.name().isEmpty() ? method.getName() : cmdAnnot.name();
        return new SimpleCommandDescriptor(name, cmdAnnot.desc(), cmdAnnot.alias());
    }

    @Override
    public boolean run(CommandCall call, List<String> parentCalls)
    {
        boolean ran = super.run(call, parentCalls);
        if (!ran)
        {
            ran = this.run(this.buildContext(this.parseCall(call), parentCalls));
        }
        return ran;
    }

    protected final CommandCall parseCall(CommandCall call)
    {
        call.setProperty(new ParsedParameters());
        this.ctxDescriptor.parseParameter(call);
        return call;
    }

    protected ParameterGroup createContextDescriptor(Method method)
    {
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
                nPosList.add(this.createParameter(param));
            }
            for (Param param : params.positional())
            {
                Parameter parameter = this.createParameter(param);
                // TODO set valuelabel if not set but "needed"
                parameter.setProperty(new FixedPosition(posList.size()));
                posList.add(parameter);
            }
        }
        return new ParameterGroup(flagsList, nPosList, posList);
    }

    private Parameter createParameter(Param param)
    {
        SimpleParameter parameter = new SimpleParameter();
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
        parameter.setProperty(new ValueType(type));
        parameter.setProperty(new ValueReader(reader));

        parameter.setProperty(param.req() ? Required.REQUIRED : Required.OPTIONAL);

        String desc = param.desc();
        if (!desc.isEmpty())
        {
            parameter.setProperty(new Description(desc));
        }

        // TODO completer
        return parameter;
    }

    /**
     * Runs this command with given CommandContext
     *
     * @param commandContext the CommandContext
     * @return whether the command was executed succesfully
     */
    protected boolean run(CommandContext commandContext)
    {
        try
        {
            Object result = this.method.invoke(holder, commandContext);
            if (result == null)
            {
                return true;
            }
            else if (result instanceof Boolean)
            {
                return (Boolean)result;
            }
            else
            {
                // TODO CommandResult
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalArgumentException(e); // TODO
        }
        return false;
    }

    protected CommandContext buildContext(CommandCall call, List<String> parentCalls)
    {
        return new ParameterizedContext(call, parentCalls);
    }
}
