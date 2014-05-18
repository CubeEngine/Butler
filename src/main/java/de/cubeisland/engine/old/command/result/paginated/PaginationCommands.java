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
package de.cubeisland.engine.old.command.result.paginated;

import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.reflected.Command;
import de.cubeisland.engine.command.reflected.Grouped;
import de.cubeisland.engine.command.reflected.Indexed;
import de.cubeisland.engine.command.reflected.IndexedParams;

public class PaginationCommands
{
    private PaginationManager paginationManager;

    public PaginationCommands(PaginationManager paginationManager)
    {
        this.paginationManager = paginationManager;
    }

    @Command(desc = "Display the next page of your previous command.")
    public void next(CommandContext context)
    {
        if (paginationManager.hasResult(context.getSender()))
        {
            paginationManager.getResult(context.getSender()).nextPage();
        }
        else
        {
            context.sendTranslated(NEGATIVE, "You don't have any results to show!");
        }
    }

    @Command(desc = "Display the previous page of your previous command.")
    public void prev(CommandContext context)
    {
        if (paginationManager.hasResult(context.getSender()))
        {
            paginationManager.getResult(context.getSender()).prevPage();
        }
        else
        {
            context.sendTranslated(NEGATIVE, "You don't have any results to show!");
        }
    }

    @Command(desc = "Display the given page of your previous command.")
    @IndexedParams(@Grouped(@Indexed(label = "pageNumber", type = Integer.class)))
    public void showpage(CommandContext context)
    {
        if (paginationManager.hasResult(context.getSender()))
        {
            Integer pageNumber = context.getIndexe(0);
            if (pageNumber != null)
            {
                paginationManager.getResult(context.getSender()).showPage(pageNumber - 1);
            }
            else
            {
                context.sendTranslated(NEGATIVE, "You have to call the command with a numeric parameter.");
            }
        }
        else
        {
            context.sendTranslated(NEGATIVE, "You don't have any results to show!");
        }
    }
}
