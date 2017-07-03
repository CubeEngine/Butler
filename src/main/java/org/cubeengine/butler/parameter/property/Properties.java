/*
 * The MIT License
 * Copyright © 2014 Cube Island
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

import java.lang.reflect.Field;
import org.cubeengine.butler.parameter.Parameter.Property;
import org.cubeengine.butler.parameter.argument.Completer;
import org.cubeengine.butler.parameter.parser.ParameterParser;
import org.cubeengine.butler.parameter.argument.ArgumentParser;
import org.cubeengine.butler.parameter.argument.DefaultValue;
import org.cubeengine.butler.parametric.builder.parameter.LabelProvider;

public class Properties
{
    public static final Property<Class<?>> TYPE = new Property<>();
    public static final Property<Class<?>> READER = new Property<>();
    public static final Property<ArgumentParser> ARGUMENT_PARSER = new Property<>();
    public static final Property<ParameterParser> PARSER = new Property<>();
    public static final Property<Filters> FILTERS = new Property<>();
    public static final Property<Integer> FIXED_POSITION = new Property<>();
    public static final Property<Field> FIELD_HOLDER = new Property<>();
    public static final Property<Integer> METHOD_INDEX = new Property<>();
    public static final Property<Integer> GREED = new Property<>();

    public static final Property<String[]> NAMES = new Property<>();
    public static final Property<String> FIXED_VALUES = new Property<>();

    public static final Property<String> FLAG_NAME = new Property<>();
    public static final Property<String> FLAG_LONGNAME = new Property<>();

    public static final Property<String> VALUE_LABEL = new Property<>();

    public static final Property<Requirement> REQUIREMENT = new Property<>();
    public static final Property<String> DESCRIPTION = new Property<>();
    public static final Property<Class<? extends Completer>> COMPLETER = new Property<>();

    public static final Property<Class<? extends DefaultValue<?>>> DEFAULT_PROVIDER = new Property<>();

    public static final Property<LabelProvider> LABEL_PROVIDER = new Property<>();
}
