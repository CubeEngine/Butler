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
package de.cubeisland.engine.command;

import java.util.Set;

import de.cubeisland.engine.command.parameter.ParameterGroup;
import de.cubeisland.engine.command.parameter.UsageGenerator;
import de.cubeisland.engine.command.parameter.property.Description;

/**
 * A simple Implementation of the {@link de.cubeisland.engine.command.CommandDescriptor}
 */
public class ImmutableCommandDescriptor extends ImmutablePropertyHolder implements CommandDescriptor
{
    @Override
    public String getName()
    {
        return valueFor(Name.class);
    }

    @Override
    public Set<String> getAliases()
    {
        return valueFor(Alias.class);
    }

    @Override
    public String getDescription()
    {
        return valueFor(Description.class);
    }

    @Override
    public String getUsage(CommandSource source)
    {
        String usage = this.valueFor(FixedUsage.class);
        if (usage == null)
        {
            UsageGenerator usageGenerator = this.valueFor(UsageProvider.class);
            if (usageGenerator != null)
            {
                usage = usageGenerator.generateUsage(source, this.valueFor(ParameterGroup.class));
            }
        }
        if (usage == null)
        {
            usage = "no usage";
        }
        return usage;
    }
}
