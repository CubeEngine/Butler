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
package de.cubeisland.engine.command.parameter.property;

import java.util.Comparator;

import de.cubeisland.engine.command.parameter.Parameter;
import de.cubeisland.engine.command.util.property.AbstractProperty;

public class MethodIndex extends AbstractProperty<Integer>
{
    /**
     * A Comparator for Parameter with MethodIndex.
     * This Comparator will throw NullPointerException when used with parameters having no MethodIndex
     */
    public static final Comparator<Parameter> COMPARATOR = new Comparator<Parameter>()
    {
        @Override
        public int compare(Parameter o1, Parameter o2)
        {
            Integer i1 = o1.valueFor(MethodIndex.class);
            Integer i2 = o2.valueFor(MethodIndex.class);
            return i1.compareTo(i2); // Null is not allowed where this comparator is used
        }
    };

    public MethodIndex(Integer value)
    {
        super(value);
    }
}
