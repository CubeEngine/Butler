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

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import de.cubeisland.engine.command.CommandManager;
import de.cubeisland.engine.command.BaseCommandSender;
import de.cubeisland.engine.command.ResultManager;

public class ConfirmManager implements ResultManager<ConfirmResult>
{
    private static final int CONFIRM_TIMEOUT = 600; // 30 seconds
    private final Map<BaseCommandSender, Queue<ConfirmResult>> confirmations;

    public ConfirmManager(CommandManager commandManager)
    {
        this.confirmations = new HashMap<BaseCommandSender, Queue<ConfirmResult>>();
        // TODO commandManager.registerCommand(new ConfirmCommand(core.getModuleManager().getCoreModule(), new BasicContextFactory(), this));
    }

    @Override
    public boolean hasResult(BaseCommandSender sender)
    {
        return this.confirmations.containsKey(sender);
    }

    @Override
    public ConfirmResult getResult(BaseCommandSender sender)
    {
        Queue<ConfirmResult> results = this.confirmations.get(sender);
        ConfirmResult poll = results.poll();
        if (results.isEmpty())
        {
            this.confirmations.remove(sender);
        }
        return poll;
    }

    @Override
    public ConfirmResult clearResult(BaseCommandSender sender)
    {
        Queue<ConfirmResult> remove = this.confirmations.remove(sender);
        if (remove != null)
        {
            return remove.poll();
        }
        return null;
    }

    @Override
    public void addResult(BaseCommandSender sender, ConfirmResult result)
    {

    }

    /*

    public synchronized void registerConfirmation(ConfirmResult confirmResult, Module module, BaseCommandSender sender)
    {
        Queue<ConfirmResult> pendingConfirmations = this.confirmations.get(sender);
        if (pendingConfirmations == null)
        {
            pendingConfirmations = new LinkedList<>();
        }
        pendingConfirmations.add(confirmResult);
        this.confirmations.put(sender, pendingConfirmations);

        Queue<Pair<Module, Integer>> confirmationTimeoutTasks = this.confirmationTimeoutTasks.get(sender);
        if (confirmationTimeoutTasks == null)
        {
            confirmationTimeoutTasks = new LinkedList<>();
        }
        confirmationTimeoutTasks.add(new Pair<>(module, this.core.getTaskManager().runTaskDelayed(module,
                                                                                                  new ConfirmationTimeoutTask(
                                                                                                      sender),
                                                                                                  CONFIRM_TIMEOUT
                                                                                                 )));
        this.confirmationTimeoutTasks.put(sender, confirmationTimeoutTasks);
    }

    public synchronized int countPendingConfirmations(@NotNull BaseCommandSender sender)
    {
        expectNotNull(sender);
        Queue<ConfirmResult> pendingConfirmations = this.confirmations.get(sender);
        if (pendingConfirmations == null)
        {
            return 0;
        }
        return pendingConfirmations.size();
    }

    public synchronized ConfirmResult getLastPendingConfirmation(BaseCommandSender sender)
    {
        if (countPendingConfirmations(sender) < 1)
        {
            return null;
        }

        Queue<Pair<Module, Integer>> confirmationTimeoutTasks = this.confirmationTimeoutTasks.get(sender);
        if (confirmationTimeoutTasks == null)
        {
            confirmationTimeoutTasks = new LinkedList<>();
        }
        Pair<Module, Integer> pair = confirmationTimeoutTasks.poll();
        this.confirmationTimeoutTasks.put(sender, confirmationTimeoutTasks);
        this.core.getTaskManager().cancelTask(pair.getLeft(), pair.getRight());

        Queue<ConfirmResult> pendingConfirmations = this.confirmations.get(sender);
        if (pendingConfirmations == null)
        {
            pendingConfirmations = new LinkedList<>();
        }
        this.confirmations.put(sender, pendingConfirmations);
        return pendingConfirmations.poll();
    }

    private class ConfirmationTimeoutTask implements Runnable
    {

        private final BaseCommandSender sender;

        private ConfirmationTimeoutTask(BaseCommandSender sender)
        {
            this.sender = sender;
        }

        @Override
        public void run()
        {
            sender.sendTranslated(NEGATIVE, "Your confirmation timed out....");
            confirmations.remove(sender);
        }
    }
    */
}
