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

import org.cubeengine.butler.completer.Completer;
import org.cubeengine.butler.parameter.ParameterParser;
import org.cubeengine.butler.parameter.reader.ArgumentReader;
import org.cubeengine.butler.parameter.reader.DefaultValue;
import org.cubeengine.butler.parametric.builder.parameter.LabelProvider;

public class Properties
{
    public static final String TYPE = "TYPE";
    public static final String READER = "READER";
    public static final Class<ArgumentReader> VALUE_READER = ArgumentReader.class;
    public static final Class<ParameterParser> PARSER = ParameterParser.class;
    public static final Class<Filters> FILTERS = Filters.class;
    public static final String FIXED_POSITION = "FIXED_POSITION";
    public static final String FIELD_HOLDER = "FIELD_HOLDER";
    public static final String METHOD_INDEX = "METHOD_INDEX";
    public static final String GREED = "GREED";

    public static final String NAMES = "NAMES";
    public static final String FIXED_VALUES = "FIXED_VALUES";

    public static final String FLAG_NAME = "FLAG_NAME";
    public static final String FLAG_LONGNAME = "FLAG_LONGNAME";

    public static final String VALUE_LABEL = "VALUE_LABEL";

    public static final Class<Requirement> REQUIREMENT = Requirement.class;
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final Class<Completer> COMPLETER = Completer.class;

    public static final Class<DefaultValue> DEFAULT_PROVIDER = DefaultValue.class;

    public static final Class<LabelProvider> LABEL_PROVIDER = LabelProvider.class;
}
