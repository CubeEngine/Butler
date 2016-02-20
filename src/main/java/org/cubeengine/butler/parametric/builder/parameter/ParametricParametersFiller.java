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
package org.cubeengine.butler.parametric.builder.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cubeengine.butler.builder.parameter.ParametersFiller;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parameter.GroupParser;
import org.cubeengine.butler.parameter.ParameterParser.ParameterType;
import org.cubeengine.butler.parameter.property.Properties;
import org.cubeengine.butler.parametric.Group;
import org.cubeengine.butler.parametric.InvokableMethod;
import org.cubeengine.butler.parametric.ParametricCommandDescriptor;

public class ParametricParametersFiller extends ParametersFiller<ParametricCommandDescriptor, InvokableMethod>
{

    @Override
    public void fill(ParametricCommandDescriptor descriptor, InvokableMethod origin)
    {
        descriptor.setParameters(buildParameters(descriptor, origin));
    }

    protected Parameter buildParameters(ParametricCommandDescriptor descriptor, InvokableMethod origin)
    {
        Method method = origin.getMethod();
        List<Type> types = Arrays.asList(method.getGenericParameterTypes());
        List<Annotation[]> annotationsList = Arrays.asList(method.getParameterAnnotations());
        return createGroup(descriptor.getContextParameter(), types, annotationsList, null, null, method);
    }

    private Parameter createGroup(int contextParameter, List<Type> types, List<Annotation[]> annotationsList, List<Field> fields,
                                  Class groupType, Method methodType)
    {
        List<Parameter> parameters = new ArrayList<>();

        int posList = 0;
        for (int i = contextParameter + 1; i < types.size(); i++)
        {
            final Type type = types.get(i);
            final Annotation[] annotations = annotationsList.get(i);
            Parameter param = createParameter(fields, methodType, i, type, annotations);
            if (param.getParameterType() == ParameterType.INDEXED)
            {
                param.offer(Properties.FIXED_POSITION, posList++);
            }
            parameters.add(param);
        }

        Parameter parameter = new Parameter();
        parameter.offer(Properties.PARSER, new GroupParser(parameter, groupType, parameters));
        return parameter;
    }

    private Parameter createParameter(List<Field> fields, Method method, int index, Type type, Annotation[] annotations)
    {
        Parameter param;
        if (fields == null)
        {
            param = createParameter(type, annotations, Java8Util.getJavaParameter(method, index));
            param.offer(Properties.METHOD_INDEX, index);

        }
        else
        {
            param = createParameter(type, annotations, new FieldLabelProvider(fields.get(index)));
            param.offer(Properties.FIELD_HOLDER, fields.get(index));
        }
        return param;
    }

    @SuppressWarnings("unchecked")
    protected Parameter createParameter(Type type, Annotation[] annotations, LabelProvider labelProvider)
    {
        if (type instanceof Class && Group.class.isAssignableFrom(((Class)type)))
        {
            List<Type> parameterTypes = new ArrayList<>();
            List<Annotation[]> paramAnnotations = new ArrayList<>();
            List<Field> fields = new ArrayList<>();
            for (Field field : ((Class)type).getFields())
            {
                if (Modifier.isPublic(field.getModifiers()))
                {
                    parameterTypes.add(field.getGenericType());
                    paramAnnotations.add(field.getAnnotations());
                    fields.add(field);
                }
            }
            return createGroup(-1, parameterTypes, paramAnnotations, fields, ((Class)type), null);
        }

        Parameter parameter = new Parameter();
        parameter.offer(Properties.LABEL_PROVIDER, labelProvider);
        return fillProperties(parameter, type, annotations);
    }


}
