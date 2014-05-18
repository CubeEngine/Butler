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
package de.cubeisland.engine.old.command.result.confirm;

import de.cubeisland.engine.command.BaseCommand;
import de.cubeisland.engine.command.CommandContext;
import de.cubeisland.engine.command.CommandManager;
import de.cubeisland.engine.command.CommandOwner;
import de.cubeisland.engine.command.CommandResult;
import de.cubeisland.engine.command.ContextFactory;
import de.cubeisland.engine.core.module.Module;
import de.cubeisland.engine.old.command.CubeCommand;

import static de.cubeisland.engine.core.util.formatter.MessageType.NEGATIVE;
import static de.cubeisland.engine.core.util.formatter.MessageType.NEUTRAL;

public class ConfirmCommand extends BaseCommand
{
    private final ConfirmManager confirmManager;

    public ConfirmCommand(CommandManager manager, CommandOwner owner)
    {
        super(manager, owner, "confirm", "Confirms a command", new ContextFactory(), null);
        this.confirmManager = new ConfirmManager(manager);
    }

    @Override
    public CommandResult run(CommandContext context)
    {
        int pendingConfirmations = confirmManager.countPendingConfirmations(context.getSender());
        if (pendingConfirmations < 1)
        {
            context.sendTranslated(NEGATIVE, "You don't have any pending confirmations!");
            return null;
        }
        confirmManager.getLastPendingConfirmation(context.getSender()).run();
        pendingConfirmations = confirmManager.countPendingConfirmations(context.getSender());
        if (pendingConfirmations > 0)
        {
            context.sendTranslated(NEUTRAL, "You have {amount} pending confirmations", pendingConfirmations);
        }
        return null;
    }
}
