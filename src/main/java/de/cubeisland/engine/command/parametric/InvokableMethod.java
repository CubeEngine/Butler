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
package de.cubeisland.engine.command.parametric;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A Method and its holder
 */
public class InvokableMethod
{
    private final Method method;
    private final Object holder;

    public InvokableMethod(Method method, Object holder)
    {
        this.method = method;
        this.holder = holder;
    }

    /**
     * Returns the Method
     *
     * @return the Method
     */
    public Method getMethod()
    {
        return method;
    }

    /**
     * Returns the Holder
     *
     * @return the holder
     */
    public Object getHolder()
    {
        return holder;
    }

    /**
     * Invokes the Method with given arguments
     *
     * @param args the arguments
     *
     * @return the returned object
     */
    @SuppressWarnings("unchecked")
    public <T> T invoke(Object... args) throws InvocationTargetException, IllegalAccessException
    {
        return (T)method.invoke(holder, args);
    }
}
