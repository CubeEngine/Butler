/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anselm Brehme, Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cubeisland.engine.old.command.result.paginated;

import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.reflected.annotation.Command;
import de.cubeisland.engine.command.reflected.annotation.Grouped;
import de.cubeisland.engine.command.reflected.annotation.Indexed;
import de.cubeisland.engine.command.reflected.annotation.IndexedParams;

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
            // TODO    context.sendTranslated(NEGATIVE, "You don't have any results to show!");
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
            // TODO  context.sendTranslated(NEGATIVE, "You don't have any results to show!");
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
                // TODO        context.sendTranslated(NEGATIVE, "You have to call the command with a numeric parameter.");
            }
        }
        else
        {
            // TODO   context.sendTranslated(NEGATIVE, "You don't have any results to show!");
        }
    }
}
