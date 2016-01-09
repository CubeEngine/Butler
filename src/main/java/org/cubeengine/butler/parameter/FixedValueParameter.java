/**
 * The MIT License
 * Copyright (c) 2014 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cubeengine.butler.parameter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cubeengine.butler.CommandInvocation;

public class FixedValueParameter extends SimpleParameter
{
    private Map<String, Object> fixedValues = new HashMap<>();

    public FixedValueParameter(Class<? extends FixedValues> type, Class<?> reader)
    {
        super(type, reader, 1);
        try
        {
            Method valuesMethod = type.getMethod("values");
            for (FixedValues value : (FixedValues[])valuesMethod.invoke(null))
            {
                fixedValues.put(value.getName(), value);
            }
        }
        catch (ReflectiveOperationException e)
        {
            throw new IllegalArgumentException("Error while extracting Fixed Values from class", e);
        }
    }

    public FixedValueParameter(String[] names)
    {
        super(String.class, String.class, 1);
        for (String name : names)
        {
            this.fixedValues.put(name, name);
        }
    }

    @Override
    protected boolean isPossible(CommandInvocation invocation)
    {
        String token = invocation.currentToken();
        for (String key : fixedValues.keySet())
        {
            if (key.equalsIgnoreCase(token))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected List<String> getSuggestions(CommandInvocation invocation)
    {
        List<String> result = new ArrayList<>();
        String token = invocation.currentToken().toLowerCase();
        for (String key : fixedValues.keySet())
        {
            if (key.startsWith(token))
            {
                result.add(key);
            }
        }
        return result;
    }

    @Override
    public void parse(CommandInvocation invocation, List<ParsedParameter> params, List<Parameter> suggestions)
    {
        String token = invocation.consume(1);
        for (String key : fixedValues.keySet())
        {
            if (key.equalsIgnoreCase(token))
            {
                params.add(ParsedParameter.of(this, fixedValues.get(key), token));
                return;
            }
        }
        throw new IllegalStateException();
    }

    public Set<String> getFixedValues()
    {
        return fixedValues.keySet();
    }
}
