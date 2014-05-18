package de.cubeisland.engine.command;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommandManager implements CommandManager
{
    private final Map<Class<? extends BaseCommand>, CommandFactory> commandFactories = new HashMap<Class<? extends BaseCommand>, CommandFactory>();

    @Override
    public <T extends BaseCommand> void registerCommandFactory(CommandFactory<T> factory)
    {
        this.commandFactories.put(factory.getCommandType(), factory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseCommand> CommandFactory<T> getCommandFactory(Class<T> type)
    {
        return (CommandFactory<T>)this.commandFactories.get(type);
    }

    @Override
    public void removeCommandFactory(Class<? extends BaseCommand> cmdType)
    {
        this.commandFactories.remove(cmdType);
    }

    @Override
    public <T extends BaseCommand> void registerCommands(CommandOwner owner, Object commandHolder, Class<T> commandType,
                                                         BaseCommand... parents)
    {
        CommandFactory<T> commandFactory = this.getCommandFactory(commandType);
        if (commandFactory == null)
        {
            throw new IllegalArgumentException("There is no CommandFactory for " + commandType.getName());
        }
        for (BaseCommand command : commandFactory.parseCommands(this, owner, commandHolder))
        {
            this.registerCommand(command, parents);
        }
    }

    @Override
    public void registerCommand(BaseCommand command, BaseCommand... parents)
    {
        if (command.isRegistered())
        {
            throw new IllegalArgumentException("The given command is already registered!");
        }
        command.getContextFactory().calculateArgBounds();
        if (parents.length != 0)
        {
            for (BaseCommand parent : parents)
            {
                parent.addChild(command);
            }
        }
        else
        {
            this.registerCommand0(command);
        }
        if (command instanceof CommandHolder)
        {
            this.registerCommands(command.getOwner(), (CommandHolder)command, command);
        }
    }

    @Override
    public void registerCommands(CommandOwner owner, CommandHolder commandHolder, BaseCommand... parents)
    {
        this.registerCommands(owner, commandHolder, commandHolder.getCommandType(), parents);
    }

    protected abstract void registerCommand0(BaseCommand command); // TODO register perm
}
