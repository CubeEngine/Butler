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
package de.cubeisland.engine.command;

import java.util.Locale;

import de.cubeisland.engine.old.command.sender.ConsoleCommandSender;

/**
 * This class manages the registration of commands.
 */
public interface CommandManager
{
    void registerCommand(BaseCommand command, BaseCommand... parents);

    void registerCommands(CommandOwner owner, CommandHolder commandHolder, BaseCommand... parents);

    <T extends BaseCommand> void registerCommands(CommandOwner owner, Object commandHolder, Class<T> commandType,
                                                  BaseCommand... parents);

    BaseCommand getCommand(String name);

    void removeCommands(BaseCommand command);

    void removeCommands(Object owner);

    void removeCommands();

    boolean runCommand(BaseCommandSender sender, String commandLine);

    ConsoleCommandSender getConsoleSender();

    <T extends BaseCommand> void registerCommandFactory(CommandFactory<T> factory);

    void removeCommandFactory(Class<? extends BaseCommand> type);

    public <T extends BaseCommand> CommandFactory<T> getCommandFactory(Class<T> type);

    void logExecution(BaseCommandSender sender, BaseCommand cubeCommand, String[] args);

    void logFailed(BaseCommandSender sender, BaseCommand cubeCommand, String[] args);

    void logTabCompletion(BaseCommandSender sender, BaseCommand cubeCommand, String[] args);

    <T extends ResultManager> T getManager(Class<T> clazz);

    Locale getDefaultLocale();
}
