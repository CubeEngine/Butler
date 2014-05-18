/**
 * This file is part of CubeEngine.
 * CubeEngine is licensed under the GNU General Public License Version 3.
 *
 * CubeEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CubeEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CubeEngine.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cubeisland.engine.command;

import java.util.List;

public final class AliasCommand extends BaseCommand
{
    private final BaseCommand target;

    public AliasCommand(BaseCommand target, String name)
    {
        super(target.getCommandManager(), target.getOwner(), name, target.getDescription(), target.getContextFactory(),
              null);
        this.target = target;
    }
    // TODO override perm get?

    public BaseCommand getTarget()
    {
        return this.target;
    }

    @Override
    public ContextFactory getContextFactory()
    {
        return super.getContextFactory();
    }

    @Override
    public CommandResult run(CommandContext context)
    {
        return this.target.run(context);
    }

    @Override
    public void help(CommandContext context)
    {
        this.target.help(context);
    }

    @Override
    public List<String> tabComplete(CommandContext context)
    {
        return this.target.tabComplete(context);
    }
}
