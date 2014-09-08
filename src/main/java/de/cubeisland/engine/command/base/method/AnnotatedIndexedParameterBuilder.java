package de.cubeisland.engine.command.base.method;

import de.cubeisland.engine.command.context.parameter.BaseParameterBuilder;
import de.cubeisland.engine.command.context.parameter.IndexedParameter;

import static de.cubeisland.engine.command.context.parameter.BaseParameter.STATIC_LABEL;

public class AnnotatedIndexedParameterBuilder extends BaseParameterBuilder<IndexedParameter, Indexed>
{
    public AnnotatedIndexedParameterBuilder()
    {
        super(IndexedParameter.class);
    }

    @Override
    public AnnotatedIndexedParameterBuilder build(Indexed source)
    {
        this.begin();

        this.setType(source.type(), source.reader());

        this.setGreed(source.greed());
        this.setRequired(source.req());
        this.setDescription(source.desc());

        this.setValueLabel(source.label());
        if (STATIC_LABEL.equals(source.label())) // Indexed Parameter uses Static Labels
        {
            for (String staticValue : source.staticValues())
            {
                this.addStaticValueLabel(staticValue, String.class);
            }
        }

        this.setCompleter(source.completer());

        return this;
    }
}
