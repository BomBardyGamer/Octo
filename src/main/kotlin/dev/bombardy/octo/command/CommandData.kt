/*
 * Octo, the simple yet responsive JDA command framework with advanced capabilities
 * Copyright (C) 2020 Callum Jay Seabrook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        val aliases: Iterable<String> = emptySet(),
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
 *
 * @author Callum Jay Seabrook
 * @since 2.0
 */
fun String.toMessage() = MessageBuilder(this).build()