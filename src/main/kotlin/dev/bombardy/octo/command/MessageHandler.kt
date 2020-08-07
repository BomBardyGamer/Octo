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

import dev.bombardy.octo.OctoException
import net.dv8tion.jda.api.entities.Message

/**
 * Does what it says in the class name, registers messages
 * Heavily based on the MessageResolver from [MFJDA](https://github.com/ipsk/MattFrameworkJDA)
 *
 * @author Callum Seabrook
 * @since 2.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class MessageHandler {

    private val messages = mutableMapOf<String, (Message) -> Unit>()

    init {
        register("commandNotFound") {
            it.channel.sendMessage("We couldn't find the command you requested.").queue()
        }
    }

    /**
     * Registers a message with the ID
     */
    fun register(messageId: String, message: (Message) -> Unit) {
        messages[messageId] = message
    }

    fun hasId(messageId: String) = messages[messageId] != null

    fun sendMessage(messageId: String, message: Message) {
        val resolver = messages[messageId]
                ?: throw OctoException("The message ID $messageId does not exist!")
        resolver(message)
    }
}