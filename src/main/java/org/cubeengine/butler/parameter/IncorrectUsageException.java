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
package org.cubeengine.butler.parameter;

import org.cubeengine.butler.exception.CommandException;

/**
 * This exception is thrown when a user performed an invalid command.
 * Use invalidUsage to throw an exception inside a command. The exception will be caught.
 */
public class IncorrectUsageException extends CommandException
{
    private final boolean displayUsage;
    
    public IncorrectUsageException()
    {
        this(true, null);
    }

    public IncorrectUsageException(String message)
    {
        this(true, message);
    }

    public IncorrectUsageException(boolean displayUsage, String message, Object... args)
    {
        super(message, args);
        this.displayUsage = displayUsage;
    }

    public boolean getDisplayUsage()
    {
        return displayUsage;
    }
}
