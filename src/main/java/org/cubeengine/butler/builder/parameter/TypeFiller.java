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
package org.cubeengine.butler.builder.parameter;


import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parameter.property.Properties;
import org.cubeengine.butler.parametric.Reader;

public class TypeFiller implements ParameterPropertyFiller
{
    @Override
    public void fill(Parameter parameter, Type type, Annotation[] annotations)
    {
        Class clazz;
        Class reader;
        if (type instanceof Class)
        {
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

        if (reader == type && Enum.class.isAssignableFrom(clazz))
        {
            reader = Enum.class; // Use default enum reader
        }

        for (Annotation annotation : annotations)
        {
            if (annotation instanceof Reader)
            {
                reader = ((Reader)annotation).value();
            }
        }

        parameter.offer(Properties.TYPE, clazz);
        parameter.offer(Properties.READER, reader);
    }
}
