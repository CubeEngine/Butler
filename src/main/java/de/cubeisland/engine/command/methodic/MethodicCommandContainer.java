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
package de.cubeisland.engine.command.methodic;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

import de.cubeisland.engine.command.Alias;
import de.cubeisland.engine.command.CommandBuilder;
import de.cubeisland.engine.command.ImmutableCommandDescriptor;
import de.cubeisland.engine.command.Name;
import de.cubeisland.engine.command.UsageProvider;
import de.cubeisland.engine.command.parameter.ParameterUsageGenerator;
import de.cubeisland.engine.command.parameter.property.Description;
import de.cubeisland.engine.command.tokenized.DispatcherCommand;
import de.cubeisland.engine.command.tokenized.SelfDescribing;

/**
 * A ContainerCommand able to dispatch methodic commands
 */
public class MethodicCommandContainer<OriginT, SubOriginT> extends DispatcherCommand implements SelfDescribing
{
    public MethodicCommandContainer(CommandBuilder<BasicMethodicCommand, SubOriginT> builder, OriginT origin)
    {
       this.registerSubCommands(builder, origin);
    }

    /**
     * Finds and registers the SubCommands of this CommandContainer
     *
     * @param builder the builder to use
     * @param origin the origin of this command
     */
    protected void registerSubCommands(CommandBuilder<BasicMethodicCommand, SubOriginT> builder, OriginT origin)
    {
        for (Method method : MethodicBuilder.getMethods(this.getClass()))
        {
            BasicMethodicCommand command = builder.buildCommand(getSubOrigin(method, origin));
            if (command != null)
            {
                this.registerCommand(command);
            }
        }
    }

    /**
     * Returns the origin for the sub command
     *
     * @param method the method of the sub command
     * @param origin the origin of this command
     * @return the Origin for the sub command
     */
    @SuppressWarnings("unchecked")
    protected SubOriginT getSubOrigin(Method method, OriginT origin)
    {
        return (SubOriginT)new InvokableMethodProperty(method, this);
    }

    @Override
    public ImmutableCommandDescriptor selfDescribe()
    {
        Command command = this.getClass().getAnnotation(Command.class);
        if (command == null)
        {
            throw new IllegalArgumentException();
        }

        ImmutableCommandDescriptor descriptor = new ImmutableCommandDescriptor();
        descriptor.setProperty(new Name(command.name()));
        descriptor.setProperty(new Description(command.desc()));
        descriptor.setProperty(new Alias(new HashSet<>(Arrays.asList(command.alias()))));
        descriptor.setProperty(new UsageProvider(new ParameterUsageGenerator()));
        return descriptor;
    }
}
