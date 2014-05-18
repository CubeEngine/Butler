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

import java.util.ArrayList;
import java.util.List;

import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.CommandResult;

public class PaginatedResult implements CommandResult
{
    private final CommandContext context;
    private final PaginationIterator iterator;

    private int pageNumber = 0;

    public PaginatedResult(CommandContext context, List<String> lines)
    {
        this.context = context;
        this.iterator = new StringListIterator(lines);

        context.getCommand().getCommandManager().getManager(PaginationManager.class).addResult(context.getSender(), this);
    }

    public PaginatedResult(CommandContext context, PaginationIterator iterator)
    {
        this.context = context;
        this.iterator = iterator;

        context.getCommand().getCommandManager().getManager(PaginationManager.class).addResult(context.getSender(), this);
    }

    @Override
    public void show(CommandContext context)
    {
        int pageCount = iterator.pageCount(PaginationManager.LINES_PER_PAGE);
        context.sendTranslated(NONE, PaginationManager.HEADER, pageNumber + 1, pageCount);
        for (String line : iterator.getPage(pageNumber, PaginationManager.LINES_PER_PAGE))
        {
            context.sendMessage(line);
        }
        if (pageNumber < 1)
        {
            if (pageCount == 1)
            {
                context.sendTranslated(NONE, PaginationManager.ONE_PAGE_FOOTER, pageNumber + 1, pageCount);
            }
            else
            {
                context.sendTranslated(NONE, PaginationManager.FIRST_FOOTER, pageNumber + 1, pageCount);
            }
        }
        else if (pageNumber >= pageCount)
        {
            context.sendTranslated(NONE, PaginationManager.LAST_FOOTER, pageNumber + 1, pageCount);
        }
        else
        {
            context.sendTranslated(NONE, PaginationManager.FOOTER, pageNumber + 1, pageCount);
        }
    }

    public void nextPage()
    {
        showPage(pageNumber + 1);
    }

    public void prevPage()
    {
        showPage(pageNumber - 1);
    }

    public void showPage(int pageNumber)
    {
        if (pageNumber >= 0 && pageNumber < iterator.pageCount(PaginationManager.LINES_PER_PAGE))
        {
            this.pageNumber = pageNumber;
            this.show(this.context);
        }
        else
        {
            context.sendTranslated(NEGATIVE, "The page you want to see is out of bounds.");
        }
    }

    private class StringListIterator implements PaginationIterator
    {
        private List<String> lines;

        public StringListIterator(List<String> lines)
        {
            this.lines = lines;
        }

        @Override
        public List<String> getPage(int page, int numberOfLines)
        {
            int offset = page * numberOfLines;
            if (offset < lines.size())
            {
                int lastItem = Math.min(offset + numberOfLines, lines.size());
                return lines.subList(offset, lastItem);
            }
            return new ArrayList<>();
        }

        @Override
        public int pageCount(int numberOfLinesPerPage)
        {
            return (int)Math.ceil((float)lines.size() / (float)numberOfLinesPerPage);
        }
    }
}
