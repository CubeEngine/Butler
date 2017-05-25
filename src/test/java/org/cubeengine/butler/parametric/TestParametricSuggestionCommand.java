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
package org.cubeengine.butler.parametric;

import java.util.Arrays;
import java.util.List;
import org.cubeengine.butler.CommandInvocation;
import org.cubeengine.butler.CommandManager;
import org.cubeengine.butler.completer.Completer;
import org.cubeengine.butler.parametric.context.BasicCommandContext;

@Command(desc = "a description")
public class TestParametricSuggestionCommand extends ParametricContainerCommand
{
    public TestParametricSuggestionCommand(CommandManager man)
    {
        super(new TestContainerDescriptor(man), TestParametricSuggestionCommand.class);
        man.getProviderManager().register(getClass(), new TestEnumCompleter(), TestEnum.class);
    }

    @Command(desc = "")
    public void parametricVal(BasicCommandContext ctx, TestEnum enumVal)
    {
        // Suggest enumVal
    }

    @Command(desc = "name ")
    public void parametricNamedVal1(BasicCommandContext ctx, @Named("name") TestEnum enumVal)
    {
        // Suggest enumVal after named
    }

    @Command(desc = "any thing name ")
    public void parametricNamedVal2(BasicCommandContext ctx, @Named("any") String any, @Named("name") TestEnum enumVal)
    {
        // Suggest enumVal as second named
    }

    @Command(desc = "any thing any2 thing ")
    public void parametricNamedVal3(BasicCommandContext ctx, @Named("any") String any, @Named("any2") String any2, @Named({"val1", "val2", "val3"}) TestEnum enumVal)
    {
        // Suggest names from second named
    }

    @Command(desc = "any thing ")
    public void parametricNamedVal4(BasicCommandContext ctx, String noCompleter, @Named("any") String any, @Named({"val1", "val2", "val3"}) TestEnum enumVal)
    {
        // Suggest names from second named, and indexed (which has none)
    }

    enum TestEnum
    {
        VALUE1,
        VALUE2
    }

    public static final List<String> TEST_LIST = Arrays.asList("val1", "val2", "val3");
    class TestEnumCompleter implements Completer
    {
        @Override
        public List<String> suggest(Class type, CommandInvocation invocation)
        {
            return TEST_LIST;
        }
    }
}
