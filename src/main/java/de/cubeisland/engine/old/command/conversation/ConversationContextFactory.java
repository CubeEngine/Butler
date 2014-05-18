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
package de.cubeisland.engine.old.command.conversation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import de.cubeisland.engine.command.BaseCommand;
import de.cubeisland.engine.command.BaseCommandSender;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.CommandFlag;
import de.cubeisland.engine.command.CommandParameter;
import de.cubeisland.engine.command.CommandParameterIndexed;
import de.cubeisland.engine.command.ContextFactory;
import de.cubeisland.engine.command.Type;

import static java.util.Locale.ENGLISH;


public class ConversationContextFactory extends ContextFactory
{
    public ConversationContextFactory()
    {
        this.addIndexed(CommandParameterIndexed.greedyIndex());
    }

    @Override
    public CommandContext parse(BaseCommand command, BaseCommandSender sender, Stack<String> labels,
                                      String[] rawArgs)
    {
        // TODO proper impl. ignoring indexed & /w flags without - in front
        final Set<String> flags = new HashSet<>();
        final Map<String, String> params = new HashMap<>();
        if (rawArgs.length > 0)
        {
            for (int offset = 0; offset < rawArgs.length; )
            {
                if (rawArgs[offset].isEmpty())
                {
                    offset++;
                    continue;
                }
                String flag = rawArgs[offset].toLowerCase(ENGLISH); // lowercase flag
                CommandFlag cmdFlag = this.getFlag(flag);
                if (cmdFlag != null) // has flag ?
                {
                    flags.add(cmdFlag.getName()); // added flag
                    offset++;
                    continue;
                } //else named param
                String paramName = rawArgs[offset].toLowerCase(ENGLISH);
                CommandParameter param = this.getParameter(paramName);
                if (param != null && offset + 1 < rawArgs.length)
                {
                    StringBuilder paramValue = new StringBuilder();
                    offset++;
                    offset += readString(paramValue, rawArgs, offset);
                    params.put(param.getName(), paramValue.toString());
                    continue;
                }
                offset++;
            }
        }
        return new CommandContext(command, sender, labels, Collections.<String>emptyList(), flags, params, Type.ANY);
    }
}
