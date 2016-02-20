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
package org.cubeengine.butler.builder.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.cubeengine.butler.CommandDescriptor;
import org.cubeengine.butler.builder.DescriptorFiller;
import org.cubeengine.butler.parameter.Parameter;

public abstract class ParametersFiller<DescriptorT extends CommandDescriptor, OriginT> implements DescriptorFiller<DescriptorT, OriginT>
{
    private final List<ParameterPropertyFiller> fillers = new ArrayList<>();

    public ParametersFiller()
    {
        fillers.add(new TypeFiller());
        fillers.add(new OptionalFiller());
        fillers.add(new DescriptionFiller());
        fillers.add(new GreedFiller());
        fillers.add(new CompleterFiller());
        fillers.add(new ParserFiller());

        fillers.add(new LabelFiller()); // after ParserFiller
        fillers.add(new DefaultFiller()); // after ParserFiller

    }

    public ParametersFiller<DescriptorT, OriginT> addFiller(ParameterPropertyFiller filler)
    {
        fillers.add(filler);
        return this;
    }


    protected Parameter fillProperties(Parameter parameter, Type type, Annotation[] annotations)
    {
        for (ParameterPropertyFiller filler : fillers)
        {
            filler.fill(parameter, type, annotations);
        }
        return parameter;
    }

}
