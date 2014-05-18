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

import java.util.HashMap;
import java.util.Map;

import de.cubeisland.engine.command.BaseCommandSender;
import de.cubeisland.engine.command.ResultManager;

public class PaginationManager implements ResultManager<PaginatedResult>
{
    public static final String HEADER = "--------- page {integer}/{integer} ---------";
    public static final String FOOTER = "- /prev - page {integer}/{integer} - /next -";
    public static final String FIRST_FOOTER = "--------- page {integer}/{integer} - /next -";
    public static final String LAST_FOOTER = "- /prev - page {integer}/{integer} ---------";
    public static final String ONE_PAGE_FOOTER = "--------- page {integer}/{integer} ---------";
    public static final int LINES_PER_PAGE = 5;

    private Map<BaseCommandSender, PaginatedResult> resultMap = new HashMap<>();

    public PaginatedResult getResult(BaseCommandSender sender)
    {
        return resultMap.get(sender);
    }

    @Override
    public PaginatedResult clearResult(BaseCommandSender sender)
    {
        return this.resultMap.remove(sender);
    }

    @Override
    public void addResult(BaseCommandSender sender, PaginatedResult result)
    {
        this.resultMap.put(sender, result);
    }

    public boolean hasResult(BaseCommandSender sender)
    {
        return this.resultMap.containsKey(sender);
    }

    // TODO implement on user unload clearResult...
}
