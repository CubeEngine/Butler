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
package org.cubeengine.butler;

import java.util.List;
import org.cubeengine.butler.parameter.enumeration.EnumName;
import org.cubeengine.butler.parameter.enumeration.SimpleEnumButler;
import org.cubeengine.butler.provider.Providers;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@EnumName("Tests")
enum TestEnum {
    AA,
    AB,
    BB,
    CC_DD
}

public class EnumerationTest
{
    private static CommandInvocation cmd(String commandLine)
    {
        return new CommandInvocation(null, commandLine, new Providers());
    }

    @Test
    public void testEnumCompleter()
    {
        List<String> expected = asList("AA", "AB");
        SimpleEnumButler simpleEnumButler = new SimpleEnumButler();
        List<String> actual = simpleEnumButler.suggest(TestEnum.class, cmd("a"));
        assertEquals(expected, actual);

    }
}
