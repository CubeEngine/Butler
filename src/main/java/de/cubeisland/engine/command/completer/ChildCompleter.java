package de.cubeisland.engine.command.completer;

import java.util.List;

import de.cubeisland.engine.command.methodic.context.BaseCommandContext;

public class ChildCompleter implements Completer<BaseCommandContext>
{
    @Override
    public List<String> complete(BaseCommandContext context, String token)
    {
        return null; // TODO
    }
}
