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
package org.cubeengine.butler.parameter.property;

import org.cubeengine.butler.parameter.NamedParameter;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parametric.Optional;
import org.cubeengine.butler.property.Property;

/**
 * Defines if the Parameter is required or not
 */
public enum Requirement implements Property<Requirement>
{
    DEFAULT,
    REQUIRED,
    OPTIONAL;

    public static boolean isRequired(Parameter parameter)
    {
        boolean angleBrackets;
        switch (parameter.valueFor(Requirement.class))
        {
            case REQUIRED:
                angleBrackets = true;
                break;
            case OPTIONAL:
                angleBrackets = false;
                break;
            case DEFAULT:
            default:
                angleBrackets = !(parameter instanceof NamedParameter);
        }
        return angleBrackets;
    }

    @Override
    public Requirement value()
    {
        return this;
    }

    public static Requirement of(Optional annotation)
    {
        return OPTIONAL;
    }


}
