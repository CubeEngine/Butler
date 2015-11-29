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
package org.cubeengine.butler.exception;

import java.util.Comparator;
import java.util.TreeSet;
import org.cubeengine.butler.CommandBase;
import org.cubeengine.butler.CommandInvocation;

/**
 * A basic ExceptionHandler forwarding to a list of ExceptionHandlers
 */
public class CompositeExceptionHandler implements ExceptionHandler
{
    private TreeSet<PriorityExceptionHandler> handlers = new TreeSet<>(new Comparator<PriorityExceptionHandler>()
    {
        @Override
        public int compare(PriorityExceptionHandler o1, PriorityExceptionHandler o2)
        {
            int compare = Integer.compare(o1.priority(), o2.priority());
            if (compare == 0)
            {
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
            return compare;
        }
    });

    /**
     * Adds an ExceptionHandler at the end of the handlerlist
     *
     * @param handler the ExceptionHandler to add
     */
    public void addHandler(PriorityExceptionHandler handler)
    {
        handlers.add(handler);
    }

    /**
     * Removes all ExceptionHandlers
     */
    public void clearHandlers()
    {
        handlers.clear();
    }

    @Override
    public boolean handleException(Throwable e, CommandBase command, CommandInvocation invocation)
    {
        for (ExceptionHandler handler : handlers)
        {
            if (handler.handleException(e, command, invocation))
            {
                return true;
            }
        }
        return false;
    }
}
