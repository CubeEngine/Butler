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
package org.cubeengine.butler.parametric;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cubeengine.butler.CommandBuilder;
import org.cubeengine.butler.alias.Alias;
import org.cubeengine.butler.alias.AliasConfiguration;
import org.cubeengine.butler.completer.CompleterProperty;
import org.cubeengine.butler.filter.Restricted;
import org.cubeengine.butler.filter.SourceFilter;
import org.cubeengine.butler.parameter.FixedValueParameter;
import org.cubeengine.butler.parameter.FixedValues;
import org.cubeengine.butler.parameter.FlagParameter;
import org.cubeengine.butler.parameter.NamedParameter;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parameter.ParameterGroup;
import org.cubeengine.butler.parameter.SimpleParameter;
import org.cubeengine.butler.parameter.UsageGenerator;
import org.cubeengine.butler.parameter.property.Description;
import org.cubeengine.butler.parameter.property.FieldHolder;
import org.cubeengine.butler.parameter.property.FixedPosition;
import org.cubeengine.butler.parameter.property.MethodIndex;
import org.cubeengine.butler.parameter.property.Requirement;
import org.cubeengine.butler.parameter.property.ValueLabel;
import org.cubeengine.butler.parameter.reader.DefaultValue;
import org.cubeengine.butler.property.Property;

import static org.cubeengine.butler.parameter.property.Requirement.OPTIONAL;

// TODO BaseAlias + Alias pre/suffix arguments
// AliasCommand
public class ParametricBuilder implements CommandBuilder<BasicParametricCommand, InvokableMethod>
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
    private UsageGenerator usageGenerator;

    public ParametricBuilder(UsageGenerator usageGenerator)
    {
        this.usageGenerator = usageGenerator;
        this.addParameterProperty(Label.class, ValueLabel.class);
        this.addParameterProperty(Optional.class, Requirement.class);
        this.addParameterProperty(Desc.class, Description.class);
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

    @Override
    public BasicParametricCommand buildCommand(InvokableMethod origin)
    {
        return this.isApplicable(origin) ? this.build(origin.getMethod().getAnnotation(Command.class), origin) : null;
    }

    protected boolean isApplicable(InvokableMethod method)
    {
        return method.getMethod().isAnnotationPresent(Command.class) && method.getMethod().getParameterTypes().length >= 1;
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

    protected BasicParametricCommand build(Command annotation, InvokableMethod origin)
    {
        return new BasicParametricCommand(fillDescriptor(newDescriptor(), annotation, origin));
    }

    @SuppressWarnings("unchecked")
    protected ParametricCommandDescriptor newDescriptor()
    {
        return new ParametricCommandDescriptor();
    }

    protected ParametricCommandDescriptor fillDescriptor(ParametricCommandDescriptor descriptor, Command annotation, final InvokableMethod origin)
    {
        descriptor.setName(annotation.name().isEmpty() ? origin.getMethod().getName() : annotation.name());
        descriptor.setDescription(annotation.desc());
        descriptor.setInvokableMethod(new InvokableMethod(origin.getMethod(), origin.getHolder()));
        descriptor.setUsageGenerator(usageGenerator);

        List<AliasConfiguration> aliasList = new ArrayList<>();
        for (String name : annotation.alias())
        {
            aliasList.add(new AliasConfiguration(name));
        }
        Alias alias = origin.getMethod().getAnnotation(Alias.class);
        if (alias != null)
        {
            for (String name : alias.value())
            {
                AliasConfiguration aliasConf = new AliasConfiguration(name, alias.parents());
                aliasConf.setPrefix(alias.prefix());
                aliasConf.setSuffix(alias.suffix());
                aliasList.add(aliasConf);
            }
        }
        descriptor.addAliases(aliasList);

        Restricted restricted = origin.getMethod().getAnnotation(Restricted.class);
        if (restricted != null)
        {
            descriptor.addFilter(new SourceFilter(restricted.value(), restricted.msg()));
        }

        descriptor.setParameters(buildParameters(descriptor, origin));
        return descriptor;
    }

    protected ParameterGroup buildParameters(ParametricDescriptor descriptor, InvokableMethod origin)
    {
        Method method = origin.getMethod();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Type[] parameterTypes = method.getGenericParameterTypes();
        List<Parameter> flagsList = new ArrayList<>();
        List<Parameter> nPosList = new ArrayList<>();
        List<Parameter> posList = new ArrayList<>();

        for (int i = 1; i < parameterTypes.length; i++)
        {
            Type paramType = parameterTypes[i];
            Annotation[] annotations = paramAnnotations[i];
            Parameter param = this.createParameter(paramType, annotations, getJavaParameter(method, i));
            param.setProperty(new MethodIndex(i));

            if (param instanceof FlagParameter)
            {
                flagsList.add(param);
            }
            else if (param instanceof NamedParameter)
            {
                nPosList.add(param);
            }
            else 
            {
                param.setProperty(new FixedPosition(posList.size()));
                posList.add(param);
            }
        }
        return new ParameterGroup(null, flagsList, nPosList, posList);
    }

    private LabelProvider getJavaParameter(Method method, int index)
    {
        if (PARAMETERS == null || PARAMETER_NAME == null)
        {
            return new LabelProvider()
            {
                @Override
                public String getLabel()
                {
                    return null;
                }
            };
        }
        try
        {
            Object paramArray = PARAMETERS.invoke(method);
            return new ParameterName(Array.get(paramArray, index));
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    interface LabelProvider
    {
        String getLabel();
    }

    class ParameterName implements LabelProvider
    {
        private Object javaParameter;

        public ParameterName(Object javaParameter)
        {
            this.javaParameter = javaParameter;
        }

        @Override
        public String getLabel()
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
    }

    class FieldName implements LabelProvider
    {
        private Field field;

        public FieldName(Field field)
        {
            this.field = field;
        }

        @Override
        public String getLabel()
        {
            return field.getName();
        }
    }

    @SuppressWarnings("unchecked")
    protected Parameter createParameter(Type type, Annotation[] annotations, LabelProvider labelProvider)
    {
        Class<?> reader;
        Class clazz;
        if (type instanceof Class)
        {
            if (Group.class.isAssignableFrom((Class)type))
            {
                return createGroupParameter((Class)type, annotations, labelProvider);
            }
            reader = ((Class)type);
            clazz = ((Class)type);
        }
        else if (type instanceof ParameterizedType)
        {
            reader = ((Class)((ParameterizedType)type).getRawType());
            Type[] typeArgs = ((ParameterizedType)type).getActualTypeArguments();
            if (typeArgs.length != 1)
            {
                throw new UnsupportedOperationException("Only exactly one generic Parameter Type is supported");
            }
            clazz = ((Class)typeArgs[0]);
        }
        else
        {
            throw new UnsupportedOperationException("Type is not supported: " + type);
        }

        List<Property> properties = new ArrayList<>();
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
                if (defaultProvider == DefaultValue.class)
                {
                    defaultProvider = clazz;
                }
                properties.add(OPTIONAL);
            }
        }
        if (reader == type && Enum.class.isAssignableFrom(clazz))
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
                longName = labelProvider.getLabel();
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
                parameter = new FixedValueParameter((Class<? extends FixedValues>)type, reader);
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
            parameter.setProperty(new ValueLabel(labelProvider.getLabel()));
        }
        return parameter;
    }

    private Parameter createGroupParameter(Class type, Annotation[] annotations, LabelProvider labelProvider)
    {
        List<Parameter> posList = new ArrayList<>();
        List<Parameter> nameList = new ArrayList<>();
        List<Parameter> flagList = new ArrayList<>();
        for (Field field : type.getFields())
        {
            if (Modifier.isPublic(field.getModifiers()))
            {
                Parameter param = createParameter(field.getGenericType(), field.getAnnotations(), labelProvider);
                param.setProperty(new FieldHolder(field));
                if (param instanceof FlagParameter)
                {
                    flagList.add(param);
                }
                else if (param instanceof NamedParameter)
                {
                    nameList.add(param);
                }
                else
                {
                    param.setProperty(new FixedPosition(posList.size()));
                    posList.add(param);
                }
            }
        }

        return new ParameterGroup(type, flagList, nameList, posList);
    }
}
