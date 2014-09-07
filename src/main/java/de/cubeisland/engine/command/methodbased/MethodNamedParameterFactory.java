package de.cubeisland.engine.command.methodbased;

import de.cubeisland.engine.command.context.parameter.NamedParameter;
import de.cubeisland.engine.command.context.parameter.NamedParameterFactory;

import static de.cubeisland.engine.command.context.parameter.BaseParameter.STATIC_LABEL;

public class MethodNamedParameterFactory extends NamedParameterFactory<NamedParameter, Named>
{
    public MethodNamedParameterFactory()
    {
        super(NamedParameter.class);
    }

    @Override
    public MethodNamedParameterFactory build(Named source)
    {
        this.begin();

        this.setType(source.type(), source.reader());

        this.setGreed(1);
        this.setRequired(source.required());
        this.setDescription(source.desc());

        this.setValueLabel(source.label());

        if (STATIC_LABEL.equals(source.label())) // Indexed Parameter uses Static Labels
        {
            for (String staticValue : source.staticValues())
            {
                this.addStaticValueLabel(staticValue, this.param().getReader());
            }
        }

        this.setName(source.name());
        for (String alias : source.alias())
        {
            this.addAlias(alias);
        }

        this.setCompleter(source.completer());

        return this;

    }
}
