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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Java8Util
{
    static final Method PARAMETERS;
    static final Method PARAMETER_NAME;

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

    public static LabelProvider getJavaParameter(Method method, int index)
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
}
