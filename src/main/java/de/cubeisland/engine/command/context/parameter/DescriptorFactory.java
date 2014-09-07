package de.cubeisland.engine.command.context.parameter;

import de.cubeisland.engine.command.context.CtxDescriptor;

public interface DescriptorFactory<DescriptorT extends CtxDescriptor, SourceT>
{
    DescriptorT build(SourceT source);
}
