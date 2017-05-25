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
package org.cubeengine.butler.builder.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.cubeengine.butler.parameter.parser.FixedValueParser;
import org.cubeengine.butler.parameter.FixedValues;
import org.cubeengine.butler.parameter.argument.FlagParser;
import org.cubeengine.butler.parameter.parser.IndexedParser;
import org.cubeengine.butler.parameter.parser.NamedParser;
import org.cubeengine.butler.parameter.Parameter;
import org.cubeengine.butler.parameter.property.Properties;
import org.cubeengine.butler.parameter.property.Requirement;
import org.cubeengine.butler.parametric.Flag;
import org.cubeengine.butler.parametric.Named;
import org.cubeengine.butler.parametric.builder.parameter.LabelProvider;

public class ParserFiller implements ParameterPropertyFiller
{
    @Override
    public void fill(Parameter parameter, Type type, Annotation[] annotations)
    {
        boolean isFlag = false;
        boolean isNamed = false;
        for (Annotation annotation : annotations)
        {
            if (annotation instanceof Flag)
            {
                if (isNamed)
                {
                    throw new IllegalArgumentException("Parameter cannot be Flag and Named");
                }
                fillFlag((Flag)annotation, parameter);
                isFlag = true;
            }
            if (annotation instanceof Named)
            {
                if (isFlag)
                {
                    throw new IllegalArgumentException("Parameter cannot be Flag and Named");
                }
                fillNamed((Named)annotation, parameter);
                isNamed = true;
            }
        }
        if (!isFlag && !isNamed)
        {
            fillIndexed(parameter);
        }
    }

    private void fillIndexed(Parameter parameter)
    {
        Class<?> clazz = parameter.getType();
        if (clazz.isEnum() && FixedValues.class.isAssignableFrom(clazz))
        {
            if (parameter.getGreed() != 1)
            {
                throw new IllegalArgumentException("Fixed Values can only have a greed of 1");
            }
            parameter.offer(Properties.PARSER, new FixedValueParser(parameter, (Class<? extends FixedValues>)clazz));

        }
        else
        {
            parameter.offer(Properties.PARSER, new IndexedParser(parameter));
        }
    }

    private void fillNamed(Named annotation, Parameter parameter)
    {
        parameter.offer(Properties.PARSER, new NamedParser(parameter, annotation.value()));
    }

    private void fillFlag(Flag annotation, Parameter parameter)
    {
        String shortName = annotation.name();
        String longName = annotation.longName();
        if (shortName.isEmpty() && longName.isEmpty())
        {
            LabelProvider labelProvider = parameter.getProperty(Properties.LABEL_PROVIDER);
            longName = labelProvider.getLabel();
            shortName = longName.substring(0, 1);
        }
        parameter.offer(Properties.FLAG_LONGNAME, longName);
        parameter.offer(Properties.FLAG_NAME, shortName);
        parameter.offer(Properties.PARSER, new org.cubeengine.butler.parameter.parser.FlagParser(parameter));
        parameter.offer(Properties.REQUIREMENT, Requirement.OPTIONAL);
        parameter.offer(Properties.DEFAULT_PROVIDER, FlagParser.class);
        parameter.offer(Properties.VALUE_READER, new FlagParser(shortName, longName));
    }
}
