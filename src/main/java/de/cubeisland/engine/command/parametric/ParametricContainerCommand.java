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
package de.cubeisland.engine.command.parametric;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import de.cubeisland.engine.command.CommandBuilder;
import de.cubeisland.engine.command.CommandDescriptor;
import de.cubeisland.engine.command.CommandInvocation;
import de.cubeisland.engine.command.DispatcherCommand;
import de.cubeisland.engine.command.alias.Alias;
import de.cubeisland.engine.command.alias.AliasConfiguration;
import de.cubeisland.engine.command.parameter.UsageGenerator;

/**
 * A ContainerCommand able to dispatch methodic commands
 */
public class ParametricContainerCommand<OriginT extends InvokableMethod> extends DispatcherCommand
{
    private CommandBuilder<BasicParametricCommand, OriginT> builder;

    public ParametricContainerCommand(ContainerCommandDescriptor descriptor, CommandBuilder<BasicParametricCommand, OriginT> builder)
    {
        super(descriptor);
        this.builder = builder;

        Command annotation = this.getClass().getAnnotation(Command.class);
        if (annotation == null)
        {
            throw new IllegalArgumentException("Missing Command annotation");
        }

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
    public void registerSubCommands()
    {
        for (Method method : ParametricBuilder.getMethods(this.getClass()))
        {
            BasicParametricCommand command = builder.buildCommand(originFor(method));
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
    protected OriginT originFor(Method method)
    {
        return (OriginT)new InvokableMethod(method, this);
    }
}
