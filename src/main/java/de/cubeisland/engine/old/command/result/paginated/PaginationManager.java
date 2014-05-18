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
