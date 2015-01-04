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
package de.cubeisland.engine.command.methodic;

import de.cubeisland.engine.command.CommandBuilder;
import de.cubeisland.engine.command.methodic.context.BaseCommandContext;
import de.cubeisland.engine.command.methodic.context.ParameterizedContext;
import de.cubeisland.engine.command.methodic.parametric.Greed;

import static de.cubeisland.engine.command.parameter.Parameter.INFINITE;


@Command(desc = "a description")
public class TestContainerCommand extends MethodicCommandContainer<Void, InvokableMethod>
{
    public TestContainerCommand(CommandBuilder<BasicMethodicCommand, InvokableMethod> builder)
    {
        super(builder, null);
    }

    @Command(desc = "a methodic command")
    @Params(positional = @Param(greed = INFINITE))
    public boolean methodic(ParameterizedContext ctx)
    {
        return ctx.getStrings(0).equals(ctx.getInvocation().getCommandLine());
    }

    @Command(desc = "a parametric command")
    public boolean parametric(BaseCommandContext ctx, @Greed(INFINITE)String aString)
    {
        return aString.equals(ctx.getInvocation().getCommandLine());
    }
}
