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
package org.cubeengine.butler.builder;

import java.util.ArrayList;
import java.util.List;
import org.cubeengine.butler.CommandBase;
import org.cubeengine.butler.CommandDescriptor;
import org.cubeengine.butler.Dispatcher;

public abstract class AbstractCommandBuilder<OriginT, DescriptorT extends CommandDescriptor> implements CommandBuilder<OriginT>
{
    private DescriptorCreator<DescriptorT> creator;
    private List<DescriptorFiller<DescriptorT, OriginT>> fillers = new ArrayList<>();

    public AbstractCommandBuilder(DescriptorCreator<DescriptorT> creator)
    {
        this.creator = creator;
    }

    @Override
    public final CommandBase buildCommand(Dispatcher base, OriginT origin)
    {
        return isApplicable(origin) ? this.build(base, buildDescriptor(origin)) : null;
    }

    protected DescriptorT newDescriptor()
    {
        return creator.create();
    }

    protected abstract CommandBase build(Dispatcher base, DescriptorT descriptor);

    protected DescriptorT buildDescriptor(OriginT origin)
    {
        DescriptorT descriptor = newDescriptor();
        for (DescriptorFiller<DescriptorT, OriginT> filler : fillers)
        {
            filler.fill(descriptor, origin);
        }
        return descriptor;
    }

    public AbstractCommandBuilder<OriginT, DescriptorT> addFiller(DescriptorFiller<DescriptorT, OriginT> filler)
    {
        fillers.add(filler);
        return this;
    }
}
