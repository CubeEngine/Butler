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
