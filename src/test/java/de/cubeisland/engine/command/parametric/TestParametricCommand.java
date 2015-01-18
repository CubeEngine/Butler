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
package de.cubeisland.engine.command.parametric;

import de.cubeisland.engine.command.CommandBuilder;
import de.cubeisland.engine.command.parametric.context.BasicCommandContext;

import static de.cubeisland.engine.command.parameter.Parameter.INFINITE;
import static org.junit.Assert.assertEquals;


@Command(desc = "a description")
public class TestParametricCommand extends ParametricContainerCommand<InvokableMethod>
{
    public TestParametricCommand(CommandBuilder<BasicParametricCommand, InvokableMethod> builder)
    {
        super(new TestContainerDescriptor(), builder);
    }

    @Command(desc = "a parametric command")
    public boolean parametric(BasicCommandContext ctx, @Greed(INFINITE)String aString)
    {
        return aString.equals(ctx.getInvocation().getCommandLine());
    }
    @Command(desc = "Also a long String that gets matched completely")
    public boolean parametric1(BasicCommandContext ctx, @Greed(INFINITE) String aString)
    {
        assertEquals(aString, ctx.getInvocation().getCommandLine());
        return true;
    }

    @Command(desc = "Value1 Value2")
    public boolean parametric2(BasicCommandContext ctx, TestEnum aEnum, TestEnum aEnum2)
    {
        assertEquals(aEnum, TestEnum.VALUE1);
        assertEquals(aEnum2, TestEnum.VALUE2);
        return true;
    }

    enum TestEnum
    {
        VALUE1,
        VALUE2
    }
}
