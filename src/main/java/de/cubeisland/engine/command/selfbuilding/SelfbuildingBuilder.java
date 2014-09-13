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
package de.cubeisland.engine.command.selfbuilding;

import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.Command;
import de.cubeisland.engine.command.CommandBase;
import de.cubeisland.engine.command.CommandBuilder;
import de.cubeisland.engine.command.CommandDescriptor;

/**
 * A CommandBuilder to be called from inside of a command
 * The command information are extracted from the annotation on the objects class
 */
public class SelfbuildingBuilder implements CommandBuilder
{
    @Override
    public List<CommandBase> buildCommands(Object object)
    {
        if (object instanceof CommandBase)
        {
            CommandDescriptor descriptor = ((CommandBase)object).getDescriptor();
            if (descriptor instanceof MutableCommandDescriptor)
            {
                Command command = object.getClass().getAnnotation(Command.class);
                ((MutableCommandDescriptor)descriptor).setName(command.name());
                ((MutableCommandDescriptor)descriptor).setDescription(command.desc());
                ((MutableCommandDescriptor)descriptor).setAliases(command.alias());
            }

            if (object instanceof ContainerCommand)
            {
                // TODO search for methodic Commands and register them on ContainerCommand
            }

            ArrayList<CommandBase> result = new ArrayList<>();
            result.add((CommandBase)object);
            return result;
        }
        throw new IllegalArgumentException("SelbuildingBuilder only accepts Commands implementing CommandBase");
    }
}
