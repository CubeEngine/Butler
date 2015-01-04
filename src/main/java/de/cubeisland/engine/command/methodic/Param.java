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
package de.cubeisland.engine.command.methodic;

import de.cubeisland.engine.command.completer.Completer;
import de.cubeisland.engine.command.parameter.property.Requirement;

/**
 * A Parameter
 */
public @interface Param
{
    /**
     * How many tokens are consumed by the parameter
     *
     * @return the greed
     */
    int greed() default 1;

    /**
     * The Label for the value of the parameter
     *
     * @return the label
     */
    String label() default "";

    /**
     * The (optional) fixed names of the parameter
     *
     * @return the names
     */
    String[] names() default {};

    /**
     * The Type of the parameter
     *
     * @return the type
     */
    Class type() default String.class;

    /**
     * The Readers Class of the parameter
     * If set to {@link Void} the reader class will be the same type as {@link #type()}
     *
     * @return the readers class
     */
    Class reader() default Void.class;

    /**
     * Whether the parameter is required or not
     *
     * @return the Requirement value
     */
    Requirement req() default Requirement.DEFAULT;

    /**
     * A short description of the parameter
     *
     * @return the description
     */
    String desc() default "";

    /**
     * The completer Class
     *
     * @return the completer class
     */
    Class<? extends Completer> completer() default Completer.class;
}
