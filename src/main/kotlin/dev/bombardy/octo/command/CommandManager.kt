/*
 * Octo, the simple yet responsive JDA command framework with advanced capabilities
 * Copyright (C) 2020  Callum Jay Seabrook
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.bombardy.octo.command

import dev.bombardy.octo.OctoScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * The command manager, used for registering and executing commands.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class CommandManager : ListenerAdapter() {

    protected open val messageHandler = MessageHandler()
    protected open val commands = mutableListOf<Command>()

    /**
     * Regisers the given command as a command to be executed by this
     * [CommandManager].
     *
     * @param command the command to register for execution.
     */
    fun register(command: Command) {
        commands += command
    }

    /**
     * Registers the given list of commands as commands to be executed by
     * this [CommandManager].
     *
     * @param commands the list of commands to register for execution.
     */
    fun registerAll(commands: List<Command>) = commands.forEach(this::register)

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        OctoScope.launch {
            handle(event.channel, event.message)
        }
    }

    fun registerMessage(id: String, message: (Message) -> Unit) = messageHandler.register(id, message)

    abstract suspend fun handle(channel: TextChannel, message: Message)
}