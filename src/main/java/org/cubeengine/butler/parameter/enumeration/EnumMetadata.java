/*
 * The MIT License
 * Copyright © 2014 Cube Island
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
package org.cubeengine.butler.parameter.enumeration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class EnumMetadata
{
    private final String name;
    private final Map<String, Enum<?>> constantLookup;
    private final List<String> names;

    public EnumMetadata(String name, Map<String, Enum<?>> constantLookup)
    {
        this.name = name;
        this.constantLookup = constantLookup;
        this.names = Collections.unmodifiableList(new ArrayList<>(constantLookup.keySet()));
    }

    public String getName()
    {
        return name;
    }

    public Map<String, Enum<?>> getConstantLookup()
    {
        return constantLookup;
    }

    public List<String> getNames()
    {
        return names;
    }
}
