package de.cubeisland.engine.command;

/**
 * A Manager for Results awaiting further input from the CommandSender
 *
 * @param <T> the Type of the CommandResult this Manager is for
 */
public interface ResultManager<T extends CommandResult>
{
    boolean hasResult(BaseCommandSender sender);

    T getResult(BaseCommandSender sender);

    T clearResult(BaseCommandSender sender);

    void addResult(BaseCommandSender sender, T result);
}
