/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anselm Brehme, Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cubeisland.engine.command.alias;

import java.util.Collections;
import java.util.Set;

import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.CommandSource;
import de.cubeisland.engine.command.DispatcherProperty;
import de.cubeisland.engine.command.UsageProvider;
import de.cubeisland.engine.command.util.property.Property;

public class AliasDescriptor implements CommandDescriptor
{
    private final String name;
    private final CommandDescriptor descriptor;
    private final DispatcherProperty dispatcher;

    public AliasDescriptor(String name, CommandDescriptor descriptor)
    {
        this.name = name;
        this.descriptor = descriptor;
        this.dispatcher = new DispatcherProperty();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Set<String> getAliases()
    {
        return Collections.emptySet();
    }

    @Override
    public String getUsage(CommandSource source)
    {
        return this.valueFor(UsageProvider.class).generateUsage(source, this);
    }

    @Override
    public String getDescription()
    {
        return this.descriptor.getDescription();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ValueT> ValueT valueFor(Class<? extends Property<ValueT>> clazz)
    {
        if (DispatcherProperty.class.equals(clazz))
        {
            return (ValueT)this.dispatcher;
        }
        return this.descriptor.valueFor(clazz);
    }
}
