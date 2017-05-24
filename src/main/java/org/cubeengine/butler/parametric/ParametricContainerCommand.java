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
package org.cubeengine.butler.parametric;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.cubeengine.butler.CommandBase;
import org.cubeengine.butler.ContainerCommand;
import org.cubeengine.butler.builder.CommandBuilder;
import org.cubeengine.butler.CommandDescriptor;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.DispatcherCommand;
import org.cubeengine.butler.alias.Alias;
import org.cubeengine.butler.alias.AliasConfiguration;
import org.cubeengine.butler.parameter.UsageGenerator;
import org.cubeengine.butler.parametric.builder.ParametricBuilder;

/**
 * A ContainerCommand able to dispatch methodic commands
 */
public class ParametricContainerCommand extends DispatcherCommand implements ContainerCommand
{
    public ParametricContainerCommand(ContainerCommandDescriptor descriptor, Class owner)
    {
        super(descriptor);

        Command annotation = this.getClass().getAnnotation(Command.class);
        if (annotation == null)
        {
            throw new IllegalArgumentException("Missing Command annotation");
        }

        descriptor.setOwner(owner);
        descriptor.setName(annotation.name());
        descriptor.setDescription(annotation.desc());
        descriptor.setUsageGenerator(new UsageGenerator()
        {
            @Override
            protected String generateParameterUsage(CommandInvocation invocation, CommandDescriptor descriptor)
            {
                return "<command>";
            }
        });

        List<AliasConfiguration> aliasList = new ArrayList<>();
        for (String name : annotation.alias())
        {
            aliasList.add(new AliasConfiguration(name));
        }
        Alias alias = this.getClass().getAnnotation(Alias.class);
        if (alias != null)
        {
            for (String name : alias.value())
            {
                AliasConfiguration aliasConf = new AliasConfiguration(name, alias.parents());
                aliasConf.setPrefix(alias.prefix());
                aliasConf.setSuffix(alias.suffix());
                aliasList.add(aliasConf);
            }
        }
        descriptor.addAliases(aliasList);
    }

    /**
     * Finds and registers the SubCommands of this CommandContainer
     */
    @Override
    public void registerSubCommands()
    {
        CommandBuilder<InvokableMethod> builder = getManager().getProviderManager().getBuilder(InvokableMethod.class);
        for (Method method : ParametricBuilder.getMethods(this.getClass()))
        {
            CommandBase command = builder.buildCommand(this, originFor(method));
            if (command != null)
            {
                this.addCommand(command);
            }
        }
    }

    /**
     * Returns the origin for the sub command
     *
     * @param method the method of the sub command
     * @return the Origin for the sub command
     */
    @SuppressWarnings("unchecked")
    protected InvokableMethod originFor(Method method)
    {
        return new InvokableMethod(method, this);
    }
}
