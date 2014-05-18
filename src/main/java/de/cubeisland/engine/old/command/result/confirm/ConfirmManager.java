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
package de.cubeisland.engine.old.command.result.confirm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import de.cubeisland.engine.command.BaseCommandSender;
import de.cubeisland.engine.command.CommandManager;
import de.cubeisland.engine.command.ResultManager;
import de.cubeisland.engine.core.Core;
import de.cubeisland.engine.core.contract.NotNull;
import de.cubeisland.engine.core.module.Module;
import de.cubeisland.engine.core.util.Pair;
import de.cubeisland.engine.old.command.BasicContextFactory;

import static de.cubeisland.engine.core.contract.Contract.expectNotNull;
import static de.cubeisland.engine.core.util.formatter.MessageType.NEGATIVE;

public class ConfirmManager implements ResultManager<ConfirmResult>
{
    private static final int CONFIRM_TIMEOUT = 600; // 30 seconds
    private final Map<BaseCommandSender, Queue<ConfirmResult>> confirmations;

    public ConfirmManager(CommandManager commandManager, Core core)
    {
        this.confirmations = new HashMap<>();
        commandManager.registerCommand(new ConfirmCommand(core.getModuleManager().getCoreModule(),
                                                          new BasicContextFactory(), this));
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

    /**
     * Register a Confirmation request. This will start a timer that will abort the request after 30 seconds and notify
     * the user.
     * This should only be called from the ConfirmResult itself!
     *
     * @param confirmResult The ConfirmResult to register
     * @param module        The module the ConfirmResult is registered to
     * @param sender        The user that need to confirm something
     */
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

    /**
     * Check if a commandSender has something to confirm
     */
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

    /**
     * Get the pending confirmation of the CommandSender and abort the task.
     * This can only be called once per confirmation
     */
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

    /**
     * Class to remove tasks that have timed out, and notify the CommandSender
     */
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
}
