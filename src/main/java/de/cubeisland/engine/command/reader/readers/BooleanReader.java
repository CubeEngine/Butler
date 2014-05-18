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

package de.cubeisland.engine.command.reader.readers;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import de.cubeisland.engine.command.exception.InvalidArgumentException;
import de.cubeisland.engine.command.reader.ArgumentReader;

public class BooleanReader extends ArgumentReader
{
    private final Set<String> yesStrings;
    private final Set<String> noStrings;

    public BooleanReader()
    {
        this.yesStrings = new HashSet<String>();
        this.yesStrings.add("yes");
        this.yesStrings.add("+");
        this.yesStrings.add("1");
        this.yesStrings.add("true");

        this.noStrings = new HashSet<String>();
        this.noStrings.add("no");
        this.noStrings.add("-");
        this.noStrings.add("0");
        this.noStrings.add("false");
    }

    @Override
    public Boolean read(String arg, Locale locale) throws InvalidArgumentException
    {
        arg = arg.trim().toLowerCase(locale);
        if (this.yesStrings.contains(arg))
        {
            return true;
        }
        else if (this.noStrings.contains(arg))
        {
            return false;
        }
        else
        {
            /* TODO
            String word = this.core.getI18n().translate(locale, "yes");
            if (arg.equalsIgnoreCase(word))
            {
                return true;
            }
            word = this.core.getI18n().translate(locale, "no");
            if (arg.equalsIgnoreCase(word))
            {
                return false;
            }
            */
        }
        return Boolean.parseBoolean(arg);
    }
}
