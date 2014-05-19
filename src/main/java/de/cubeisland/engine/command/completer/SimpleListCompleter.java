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

package de.cubeisland.engine.command.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.cubeisland.engine.command.BaseCommandContext;

import static de.cubeisland.engine.command.StringUtils.startsWithIgnoreCase;

public abstract class SimpleListCompleter implements Completer
{
    private final String[] strings;

    protected SimpleListCompleter(String... strings)
    {
        this.strings = strings;
    }

    @Override
    public List<String> complete(BaseCommandContext context, String token)
    {
        List<String> tokens = Arrays.asList(token.split(","));
        String lastToken = token.substring(token.lastIndexOf(",") + 1, token.length()).toUpperCase();
        List<String> offers = new ArrayList<String>();
        for (String string : this.strings)
        {
            if (startsWithIgnoreCase(string, lastToken) && !tokens.contains(string))
            {
                offers.add(token.substring(0, token.lastIndexOf(",") + 1) + string);
            }
        }
        return offers;
    }
}
