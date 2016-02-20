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
package org.cubeengine.butler.parametric.builder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cubeengine.butler.builder.AbstractCommandBuilder;
import org.cubeengine.butler.builder.DescriptorCreator;
import org.cubeengine.butler.parameter.UsageGenerator;
import org.cubeengine.butler.parametric.BasicParametricCommand;
import org.cubeengine.butler.parametric.Command;
import org.cubeengine.butler.parametric.InvokableMethod;
import org.cubeengine.butler.parametric.ParametricCommandDescriptor;
import org.cubeengine.butler.parametric.builder.parameter.ParametricParametersFiller;

// TODO BaseAlias + Alias pre/suffix arguments
// AliasCommand
public class ParametricBuilder extends AbstractCommandBuilder<BasicParametricCommand, InvokableMethod, ParametricCommandDescriptor>
{
    private ParametricParametersFiller parameterFiller;

    public ParametricBuilder(DescriptorCreator<ParametricCommandDescriptor> creator, UsageGenerator usageGenerator)
    {
        super(creator);
        this.addFiller(new CommandFiller())
            .addFiller(new AliasFiller())
            .addFiller(new RestrictedFiller())
            .addFiller(new UsageGeneratorFiller(usageGenerator))
            .addFiller(this.parameterFiller = new ParametricParametersFiller());
    }

    public ParametricBuilder(UsageGenerator usageGenerator)
    {
        this(new DescriptorCreator<ParametricCommandDescriptor>()
        {
            @Override
            public ParametricCommandDescriptor create()
            {
                return new ParametricCommandDescriptor();
            }
        }, usageGenerator);
    }

    public ParametricParametersFiller getParameterFiller()
    {
        return this.parameterFiller;
    }

    public static Set<Method> getMethods(Class holder)
    {
        HashSet<Method> methods = new LinkedHashSet<>();
        methods.addAll(Arrays.asList(holder.getMethods()));
        for (Method method : methods)
        {
            method.setAccessible(true);
        }
        methods.addAll(Arrays.asList(holder.getDeclaredMethods()));
        return methods;
    }

    @Override
    public boolean isApplicable(InvokableMethod method)
    {
        return method.getMethod().isAnnotationPresent(Command.class) && method.getMethod().getParameterTypes().length >= 1;
    }

    protected BasicParametricCommand build(ParametricCommandDescriptor descriptor)
    {
        return new BasicParametricCommand(descriptor);
    }
}
