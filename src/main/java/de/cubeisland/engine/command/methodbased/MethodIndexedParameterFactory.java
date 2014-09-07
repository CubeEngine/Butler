package de.cubeisland.engine.command.methodbased;

import de.cubeisland.engine.command.context.parameter.BaseParameterFactory;
import de.cubeisland.engine.command.context.parameter.IndexedParameter;

import static de.cubeisland.engine.command.context.parameter.BaseParameter.STATIC_LABEL;

public class MethodIndexedParameterFactory extends BaseParameterFactory<IndexedParameter, Indexed>
{
    public MethodIndexedParameterFactory()
    {
        super(IndexedParameter.class);
    }

    @Override
    public MethodIndexedParameterFactory build(Indexed source)
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
