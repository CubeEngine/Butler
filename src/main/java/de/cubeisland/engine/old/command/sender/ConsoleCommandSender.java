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
package de.cubeisland.engine.old.command.sender;

import java.util.Locale;

import org.bukkit.Server;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;

import de.cubeisland.engine.core.bukkit.BukkitCore;

public class ConsoleCommandSender extends WrappedCommandSender implements org.bukkit.command.ConsoleCommandSender
{
    public static final String NAME = ":console";
    private final Server server;

    public ConsoleCommandSender(BukkitCore core)
    {
        super(core, core.getServer().getConsoleSender());
        this.server = core.getServer();
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getDisplayName()
    {
        return this.getCore().getI18n().translate(Locale.getDefault(), "Console");
    }

    @Override
    public org.bukkit.command.ConsoleCommandSender getWrappedSender()
    {
        return this.server.getConsoleSender();
    }

    @Override
    public boolean hasPermission(String name)
    {
        return true;
    }

    @Override
    public boolean hasPermission(Permission perm)
    {
        return true;
    }

    @Override
    public boolean isConversing()
    {

        return this.getWrappedSender().isConversing();
    }

    @Override
    public void acceptConversationInput(String input)
    {
        this.getWrappedSender().acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(Conversation conversation)
    {
        return this.getWrappedSender().beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation)
    {
        this.getWrappedSender().abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details)
    {
        this.getWrappedSender().abandonConversation(conversation, details);
    }

    @Override
    public void sendRawMessage(String message)
    {
        this.getWrappedSender().sendRawMessage(message);
    }
}
