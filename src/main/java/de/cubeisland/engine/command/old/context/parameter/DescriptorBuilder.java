package de.cubeisland.engine.command.old.context.parameter;

import de.cubeisland.engine.command.old.context.CtxDescriptor;

public interface DescriptorBuilder<DescriptorT extends CtxDescriptor, SourceT>
{
    DescriptorT build(SourceT source);
}
