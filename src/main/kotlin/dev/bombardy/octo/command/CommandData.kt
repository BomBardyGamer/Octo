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

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message

/**
 * Represents options for a command
 *
 * @author Callum Seabrook
 * @since 2.0
 */
data class CommandOptions(
        val aliases: List<String> = emptyList(),
        val optionalArgs: Boolean = false,
        val allowBots: Boolean = false,
        val isSynchronous: Boolean = false
)

/**
 * Represents messages that will be sent if a command is not used correctly
 *
 * @author Callum Seabrook
 * @since 2.0
 */
data class CommandMessages(
        val help: Message = getMessage("help"),
        val noBots: Message = getMessage("noBots")
)

private val MESSAGE_DEFAULTS = mapOf(
        "help" to "Use the command properly and you wouldn't see this".toMessage(),
        "noBots" to "Sorry, bots can't execute commands!".toMessage()
)

private fun getMessage(key: String) = MESSAGE_DEFAULTS.getValue(key)

/**
 * Does what it says on the tin, converts a string to JDA's [Message] entity,
 * representing a Discord message
 */
fun String.toMessage() = MessageBuilder(this).build()