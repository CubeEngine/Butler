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
package de.cubeisland.engine.command.context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import de.cubeisland.engine.command.context.parameter.IndexedParameter;
import de.cubeisland.engine.command.context.parameter.NamedParameter;
import de.cubeisland.engine.command.context.reader.ArgumentReader;
import de.cubeisland.engine.command.exception.IncorrectArgumentException;
import de.cubeisland.engine.command.exception.ReaderException;

public class ContextReader extends ContextParser
{
    public ContextReader(CtxDescriptor descriptor)
    {
        super(descriptor);
    }

    public void readContext(ReadParameters ctx, Locale locale)
    {
        if (!ctx.rawIndexed.isEmpty())
        {
            if (ctx.rawIndexed.get(ctx.rawIndexed.size() - 1).isEmpty())
            {
                // remove last if empty (only needed for tab completion)
                ctx.rawIndexed.remove(ctx.rawIndexed.size() - 1);
                ctx.indexedCount--;
            }
        }
        ctx.indexed = this.readIndexed(locale, ctx.rawIndexed);
        ctx.named = this.readNamed(locale, ctx.rawNamed);
    }

    protected List<Object> readIndexed(Locale locale, List<String> rawIndexed)
    {
        List<Object> result = new ArrayList<>();
        int i = 0;
        for (String arg : rawIndexed)
        {
            IndexedParameter indexed = this.descriptor.indexedMap.get(i++);
            if (indexed == null)
            {
                result.add(arg); // handle OOB somewhere else
            }
            else
            {
                try
                {
                    Class<?> staticReader = indexed.getStaticReader(arg.toLowerCase());
                    if (staticReader != null)
                    {
                        result.add(ArgumentReader.read(staticReader, indexed.getType(), arg.toLowerCase(), locale));
                    }
                    else
                    {
                        result.add(ArgumentReader.read(indexed.getReader(), indexed.getType(), arg, locale));
                    }
                }
                catch (ReaderException e)
                {
                    throw new IncorrectArgumentException(i, e);
                }
            }
        }
        return result;
    }

    protected Map<String, Object> readNamed(Locale locale, Map<String, String> rawNamed)
    {
        Map<String, Object> readParams = new LinkedHashMap<>();

        for (Entry<String, String> entry : rawNamed.entrySet())
        {
            NamedParameter param = this.descriptor.namedMap.get(entry.getKey());
            try
            {
                readParams.put(entry.getKey(), ArgumentReader.read(param.getReader(), param.getType(), entry.getValue(), locale));
            }
            catch (ReaderException ex)
            {
                throw new IncorrectArgumentException(param.getName(), ex);
            }
        }
        return readParams;
    }
}
