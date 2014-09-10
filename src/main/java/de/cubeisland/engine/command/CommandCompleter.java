package de.cubeisland.engine.command;

import java.util.List;

/**
 * Provides a method allowing tab completion for commands
 */
public interface CommandCompleter
{
    /**
     * Returns a list of suggestions based on given CommandCall
     *
     * @param call the CommandCall
     *
     * @param previousTokens the previous tokens
     * @return a list of suggestions
     */
    List<String> getSuggestions(CommandCall call, List<String> previousTokens);
}
