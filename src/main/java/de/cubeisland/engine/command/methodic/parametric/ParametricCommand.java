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
package de.cubeisland.engine.command.methodic.parametric;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.CommandCall;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.Property;
import de.cubeisland.engine.command.methodic.Flag;
import de.cubeisland.engine.command.methodic.MethodicCommand;
import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.parameter.ParameterGroup;
import de.cubeisland.engine.command.parameter.ParsedParameter;
import de.cubeisland.engine.command.parameter.ParsedParameters;
import de.cubeisland.engine.command.parameter.SimpleParameter;
import de.cubeisland.engine.command.parameter.property.*;

public class ParametricCommand extends MethodicCommand
{
    public ParametricCommand(Object holder, Method method)
    {
        super(holder, method);
    }

    @Override
    protected ParameterGroup createContextDescriptor(Method method)
    {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Parameter> flagsList = new ArrayList<>();
        List<Parameter> nPosList = new ArrayList<>();
        List<Parameter> posList = new ArrayList<>();
        // TODO check if first is a context (NOT Parameterized!!!)
        for (int i = 1; i < parameterTypes.length; i++)
        {
            Class<?> parameter = parameterTypes[i];
            Annotation[] annotations = paramAnnotations[i];
            Parameter param = this.createParameter(parameter, annotations);
            param.setProperty(new MethodIndex(i));
            for (Annotation annotation : annotations) // Find type of Annotation to assign to correct list
            {
                if (annotation instanceof Flag)
                {
                    flagsList.add(param);
                    param = null;
                    break;
                }
                else if (annotation instanceof Index)
                {
                    param.setProperty(new FixedPosition(posList.size()));
                    posList.add(param);
                    param = null;
                    break;
                }
            }
            if (param != null) // if not Flag or Positional its a non-Positional
            {
                nPosList.add(param);
            }
        }
        return new ParameterGroup(flagsList, nPosList, posList);
    }

    private Parameter createParameter(Class<?> clazz, Annotation[] annotations)
    {
        if (Group.class.isAssignableFrom(clazz))
        {
            // TODO Groups
            return null;
        }


        List<Property> properties = new ArrayList<>();
        Class<?> reader = clazz;
        int greed = 1;
        for (Annotation annotation : annotations)
        {
            if (annotation instanceof Greed)
            {
                greed = ((Greed)annotation).value();
            }
            else if (annotation instanceof Label)
            {
                properties.add(new ValueLabel(((Label) annotation).value()));
            }
            else if (annotation instanceof Names)
            {
                properties.add(new FixedValues(((Names) annotation).value()));
            }
            else if (annotation instanceof Optional)
            {
                properties.add(Required.OPTIONAL);
            }
            else if (annotation instanceof Desc)
            {
                properties.add(new Description(((Desc) annotation).value()));
            }
            else if (annotation instanceof Reader)
            {
                reader = ((Reader) annotation).value();
            }
            // TODO completer
        }
        if (reader == clazz && Enum.class.isAssignableFrom(clazz))
        {
            reader = Enum.class;
        }
        SimpleParameter parameter = new SimpleParameter(clazz, reader);
        parameter.setProperties(properties.toArray(new Property[properties.size()]));
        if (parameter.propertyValue(Required.class) == null)
        {
            parameter.setProperty(Required.REQUIRED);
        }
        if (greed != 1)
        {
            parameter.setProperty(new de.cubeisland.engine.command.parameter.property.Greed(greed));
        }
        return parameter;
    }


    @Override
    protected boolean run(CommandContext commandContext)
    {
        try
        {
            Object[] args = new Object[this.method.getParameterTypes().length];
            args[0] = commandContext;
            for (ParsedParameter parameter : commandContext.getCall().propertyValue(ParsedParameters.class))
            {
                Integer methodIndex = parameter.getParameter().propertyValue(MethodIndex.class);
                if (methodIndex != null)
                {
                    args[methodIndex] = parameter.getParsedValue();
                }
            }

            Object result = this.method.invoke(holder, args);
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

    @Override
    protected CommandContext buildContext(CommandCall call, List<String> parentCalls)
    {
        return new CommandContext(call, parentCalls);
    }
}
