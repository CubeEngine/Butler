package de.cubeisland.engine.command.methodbased;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.context.CtxDescriptor;
import de.cubeisland.engine.command.context.parameter.DescriptorFactory;
import de.cubeisland.engine.command.context.parameter.FlagParameter;
import de.cubeisland.engine.command.context.parameter.IndexedParameter;
import de.cubeisland.engine.command.context.parameter.NamedParameter;
import de.cubeisland.engine.command.context.parameter.ParameterGroup;

public class MethodDescriptorFactory implements DescriptorFactory<CtxDescriptor, Method>
{
    protected final MethodIndexedParameterFactory factoryIndexed;
    protected final MethodNamedParameterFactory factoryNamed;
    protected final MethodFlagFactory factoryFlag;

    public MethodDescriptorFactory(MethodIndexedParameterFactory factoryIndexed, MethodNamedParameterFactory factoryNamed, MethodFlagFactory factoryFlag)
    {
        this.factoryIndexed = factoryIndexed;
        this.factoryNamed = factoryNamed;
        this.factoryFlag = factoryFlag;
    }

    @Override
    public CtxDescriptor build(Method source)
    {
        Indexeds indexeds = source.getAnnotation(Indexeds.class);
        ParameterGroup<IndexedParameter> groupIndexed = new ParameterGroup<>();
        if (indexeds != null)
        {
            for (Indexed indexed : indexeds.value())
            {
                groupIndexed.add(factoryIndexed.build(indexed).finish());
            }
        }
        Nameds nameds = source.getAnnotation(Nameds.class);
        ParameterGroup<NamedParameter> groupNamed = new ParameterGroup<>();
        if (nameds != null)
        {
            for (Named named : nameds.value())
            {
                groupNamed.add(factoryNamed.build(named).finish());
            }
        }
        Flags flags = source.getAnnotation(Flags.class);
        List<FlagParameter> groupFlag = new ArrayList<>();
        if (flags != null)
        {
            for (Flag flag : flags.value())
            {
                groupFlag.add(factoryFlag.build(flag).finish());
            }
        }
        return this.build(source, groupIndexed, groupNamed, groupFlag);
    }

    protected CtxDescriptor build(Method source, ParameterGroup<IndexedParameter> groupIndexed, ParameterGroup<NamedParameter> groupNamed, List<FlagParameter> groupFlag)
    {
        return new CtxDescriptor(groupIndexed, groupNamed, groupFlag);
    }

    public static MethodDescriptorFactory newInstance()
    {
        return new MethodDescriptorFactory(new MethodIndexedParameterFactory(), new MethodNamedParameterFactory(), new MethodFlagFactory());
    }
}
