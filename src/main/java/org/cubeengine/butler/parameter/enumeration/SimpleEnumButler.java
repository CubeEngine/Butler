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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.cubeengine.butler.Butler;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.CompletionHelper;
import org.cubeengine.butler.parameter.argument.ParserException;

/**
 * A Reader for UpperCased Enum names
 */
public class SimpleEnumButler implements Butler
{
    private final Map<Class, EnumMetadata> enumConstantNames = new WeakHashMap<>();

    private EnumMetadata lookupMetadata(Class<?> type)
    {
        EnumMetadata constantNames = enumConstantNames.get(type);
        if (constantNames == null)
        {
            @SuppressWarnings("unchecked")
            Enum<?>[] enumConstants = ((Class<? extends Enum<?>>)type).getEnumConstants();

            Map<String, Enum<?>> constants = new HashMap<>(enumConstants.length);
            for (final Enum<?> constant : enumConstants)
            {
                constants.put(constant.name(), constant);
            }
            String name;
            EnumName nameAnnotation = type.getAnnotation(EnumName.class);
            if (nameAnnotation != null)
            {
                name = nameAnnotation.value();
            }
            else
            {
                name = type.getSimpleName();
            }
            EnumMetadata metadata = new EnumMetadata(name, constants);
            enumConstantNames.put(type, metadata);
            return metadata;
        }
        else
        {
            return constantNames;
        }
    }

    private static boolean isEnum(Class t)
    {
        return Enum.class.isAssignableFrom(t);
    }

    private static String toEnumName(String name)
    {
        return name.replaceAll(" ", "_").toUpperCase();
    }

    @Override
    public Object parse(Class type, CommandInvocation invocation) throws ParserException
    {
        if (!isEnum(type))
        {
            throw new UnsupportedOperationException();
        }
        EnumMetadata metadata = lookupMetadata(type);
        Enum<?> constant = metadata.getConstantLookup().get(toEnumName(invocation.currentToken()));
        if (constant == null)
        {
            throw new ParserException("Could not find \"" + invocation.currentToken() + "\" in Enum");
        }
        invocation.consume(1);
        return constant;
    }

    @Override
    public List<String> suggest(Class type, CommandInvocation invocation)
    {
        if (!isEnum(type))
        {
            return Collections.emptyList();
        }
        EnumMetadata metadata = lookupMetadata(type);
        if (metadata == null)
        {
            return Collections.emptyList();
        }
        return CompletionHelper.complete(toEnumName(invocation.currentToken()), metadata.getNames());
    }
}
