/**
 * The MIT License
 * Copyright (c) 2014 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.cubeisland.engine.command.methodic.parametric;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.CommandException;
import de.cubeisland.engine.command.ImmutableCommandDescriptor;
import de.cubeisland.engine.command.completer.CompleterProperty;
import de.cubeisland.engine.command.methodic.Command;
import de.cubeisland.engine.command.methodic.Flag;
import de.cubeisland.engine.command.methodic.InvokableMethod;
import de.cubeisland.engine.command.methodic.MethodicBuilder;
import de.cubeisland.engine.command.methodic.context.BasicCommandContext;
import de.cubeisland.engine.command.parameter.FixedValueParameter;
import de.cubeisland.engine.command.parameter.FixedValues;
import de.cubeisland.engine.command.parameter.FlagParameter;
import de.cubeisland.engine.command.parameter.NamedParameter;
import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.parameter.ParameterGroup;
import de.cubeisland.engine.command.parameter.SimpleParameter;
import de.cubeisland.engine.command.parameter.property.Description;
import de.cubeisland.engine.command.parameter.property.FixedPosition;
import de.cubeisland.engine.command.parameter.property.MethodIndex;
import de.cubeisland.engine.command.parameter.property.Requirement;
import de.cubeisland.engine.command.parameter.property.ValueLabel;
import de.cubeisland.engine.command.parameter.reader.DefaultProvider;
import de.cubeisland.engine.command.util.property.Property;

import static de.cubeisland.engine.command.parameter.property.Requirement.OPTIONAL;

public class ParametricBuilder<OriginT extends InvokableMethod> extends MethodicBuilder<OriginT>
{
    private static final Method PARAMETERS;
    private static final Method PARAMETER_NAME;

    static
    {
        Method parameters = null;
        Method parameterNames = null;
        try
        {
            parameters = Method.class.getMethod("getParameters");
            parameterNames = parameters.getReturnType().getComponentType().getMethod("getName");
        }
        catch (NoSuchMethodException ignored)
        {}
        PARAMETERS = parameters;
        PARAMETER_NAME = parameterNames;
    }

    private final Map<Class<? extends Annotation>, Method> parameterProperties = new HashMap<>();

    public ParametricBuilder()
    {
        this.addParameterProperty(Label.class, ValueLabel.class);
        this.addParameterProperty(Optional.class, Requirement.class);
        this.addParameterProperty(Desc.class, Description.class);
    }

    @Override
    protected boolean isApplicable(Method method)
    {
        return method.isAnnotationPresent(Command.class) && method.getParameterTypes().length >= 1;
    }

    private void addParameterProperty(Class<? extends Annotation> annotClass, Class<? extends Property> propertyClass)
    {
        try
        {
            Method method = propertyClass.getMethod("of", annotClass);
            if (method.getReturnType() == propertyClass && Modifier.isStatic(method.getModifiers()))
            {
                this.parameterProperties.put(annotClass, method);
                return;
            }
        }
        catch (NoSuchMethodException ignored)
        {}
        throw new IllegalArgumentException("The Property needs to have a static Method of having the Annotation Class as Parameter and return an instance of the Property");
    }

    private Property propertyOf(Annotation annotation)
    {
        Method method = this.parameterProperties.get(annotation.annotationType());
        if (method == null)
        {
            return null;
        }
        try
        {
            return (Property)method.invoke(null, annotation);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected BasicParametricCommand build(Command annotation, OriginT origin)
    {
        ImmutableCommandDescriptor descriptor = buildCommandDescriptor(annotation, origin);
        descriptor.setProperty(buildParameters(descriptor, origin));
        return new BasicParametricCommand(descriptor);
    }

    @Override
    protected ParameterGroup buildParameters(CommandDescriptor descriptor, OriginT origin)
    {
        Method method = origin.getMethod();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Parameter> flagsList = new ArrayList<>();
        List<Parameter> nPosList = new ArrayList<>();
        List<Parameter> posList = new ArrayList<>();

        for (int i = 1; i < parameterTypes.length; i++)
        {
            Class<?> paramType = parameterTypes[i];
            Annotation[] annotations = paramAnnotations[i];
            Parameter param = this.createParameter(descriptor, paramType, annotations, origin, getJavaParameter(method, i));
            param.setProperty(new MethodIndex(i));
            for (Annotation annotation : annotations) // Find type of Annotation to assign to correct list
            {
                if (annotation instanceof Flag)
                {
                    flagsList.add(param);
                    param = null;
                    break;
                }
                else if (annotation instanceof Named)
                {
                    nPosList.add(param);
                    param = null;
                    break;
                }
            }
            if (param != null)
            {
                param.setProperty(new FixedPosition(posList.size()));
                posList.add(param);
            }
        }
        return new ParameterGroup(flagsList, nPosList, posList);
    }

    private Object getJavaParameter(Method method, int index)
    {
        if (PARAMETERS == null || PARAMETER_NAME == null)
        {
            return null;
        }
        try
        {
            Object paramArray = PARAMETERS.invoke(method);
            return Array.get(paramArray, index);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String javaParameterLabel(Object javaParameter)
    {
        if (PARAMETER_NAME == null)
        {
            throw new IllegalStateException("Missing Label");
        }
        try
        {
            return PARAMETER_NAME.invoke(javaParameter).toString();
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected Parameter createParameter(CommandDescriptor descriptor, Class<?> clazz, Annotation[] annotations,
                                        OriginT origin, Object javaParameter)
    {
        if (Group.class.isAssignableFrom(clazz))
        {
            // TODO Groups
            return null;
        }

        List<Property> properties = new ArrayList<>();
        Class<?> reader = clazz;
        int greed = 1;
        Flag flag = null;
        Named named = null;
        Class defaultProvider = null;
        for (Annotation annotation : annotations)
        {
            if (annotation instanceof Flag)
            {
                flag = (Flag)annotation;
            }
            if (annotation instanceof Named)
            {
                named = (Named)annotation;
            }
            Property property = this.propertyOf(annotation);
            if (property != null)
            {
                properties.add(property);
                continue;
            }
            if (annotation instanceof Greed)
            {
                greed = ((Greed)annotation).value();
            }
            else if (annotation instanceof Reader)
            {
                reader = ((Reader) annotation).value();
            }
            else if (annotation instanceof Complete)
            {
                properties.add(new CompleterProperty(((Complete)annotation).value()));
            }
            else if (annotation instanceof Default)
            {
                defaultProvider = ((Default)annotation).value();
                if (defaultProvider == DefaultProvider.class)
                {
                    defaultProvider = clazz;
                }
                properties.add(OPTIONAL);
            }
            else if (annotation instanceof Type)
            {
                clazz = ((Type)annotation).value();
            }
        }
        if (reader == clazz && Enum.class.isAssignableFrom(clazz))
        {
            reader = Enum.class;
        }
        Parameter parameter;
        if (flag != null)
        {
            String shortName = flag.name();
            String longName = flag.longName();
            if (shortName.isEmpty() && longName.isEmpty())
            {
                longName = javaParameterLabel(javaParameter);
                shortName = longName.substring(0, 1);
            }
            parameter = new FlagParameter(shortName, longName);
        }
        else if (named != null)
        {
            parameter = new NamedParameter(clazz, reader, named.value(), 1);
        }
        else
        {
            if (clazz.isEnum() && FixedValues.class.isAssignableFrom(clazz))
            {
                if (greed != 1)
                {
                    throw new IllegalArgumentException("Fixed Values can only have a greed of 1");
                }
                parameter = new FixedValueParameter((Class<? extends FixedValues>)clazz, reader);
            }
            else
            {
                parameter = new SimpleParameter(clazz, reader, greed);
            }
        }
        parameter.setDefaultProvider(defaultProvider);
        parameter.setProperties(properties.toArray(new Property[properties.size()]));
        if (parameter.valueFor(Requirement.class) == null)
        {
            parameter.setProperty(Requirement.DEFAULT);
        }
        if (parameter.valueFor(ValueLabel.class) == null)
        {
            parameter.setProperty(new ValueLabel(javaParameterLabel(javaParameter)));
        }
        return parameter;
    }

}
