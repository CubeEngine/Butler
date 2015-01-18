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
package de.cubeisland.engine.command.parametric.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import de.cubeisland.engine.command.CommandInvocation;

public class CommandContextBuilder implements ContextBuilder
{
    private Map<Class, Constructor> constructorMap = new HashMap<>();

    @Override
    public Object buildContext(CommandInvocation invocation, Class<?> parameterType)
    {
        try
        {
            Constructor constructor = constructorMap.get(parameterType);
            if (constructor == null)
            {
                try
                {
                    constructor = parameterType.getConstructor(CommandInvocation.class);
                    constructorMap.put(parameterType, constructor);
                }
                catch (NoSuchMethodException e)
                {
                    throw new IllegalArgumentException("Given class has no constructor with only a CommandInvocation");
                }
            }
            return constructor.newInstance(invocation);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
