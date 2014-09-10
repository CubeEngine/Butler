package de.cubeisland.engine.command.old.base.method;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.old.context.CtxDescriptor;
import de.cubeisland.engine.command.old.context.parameter.DescriptorBuilder;
import de.cubeisland.engine.command.old.context.parameter.FlagParameter;
import de.cubeisland.engine.command.old.context.parameter.IndexedParameter;
import de.cubeisland.engine.command.old.context.parameter.NamedParameter;
import de.cubeisland.engine.command.old.context.parameter.ParameterGroup;

public class AnnotatedDescriptorBuilder<Annot extends AnnotatedElement> implements DescriptorBuilder<CtxDescriptor, Annot>
{
    protected final AnnotatedIndexedParameterBuilder factoryIndexed;
    protected final AnnotatedNamedParameterBuilder factoryNamed;
    protected final AnnotatedFlagBuilder factoryFlag;

    public AnnotatedDescriptorBuilder(AnnotatedIndexedParameterBuilder factoryIndexed,
                                      AnnotatedNamedParameterBuilder factoryNamed, AnnotatedFlagBuilder factoryFlag)
    {
        this.factoryIndexed = factoryIndexed;
        this.factoryNamed = factoryNamed;
        this.factoryFlag = factoryFlag;
    }

    public AnnotatedDescriptorBuilder()
    {
        this(new AnnotatedIndexedParameterBuilder(), new AnnotatedNamedParameterBuilder(), new AnnotatedFlagBuilder());
    }

    @Override
    public CtxDescriptor build(Annot source)
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

    protected CtxDescriptor build(Annot source, ParameterGroup<IndexedParameter> groupIndexed, ParameterGroup<NamedParameter> groupNamed, List<FlagParameter> groupFlag)
    {
        return new CtxDescriptor(groupIndexed, groupNamed, groupFlag);
    }
}
