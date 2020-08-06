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
        internal val aliases: List<String> = emptyList(),
        internal val optionalArgs: Boolean = false,
        internal val allowBots: Boolean = false,
        internal val isSynchronous: Boolean = false
)

/**
 * Represents messages that will be sent if a command is not used correctly
 *
 * @author Callum Seabrook
 * @since 2.0
 */
data class CommandMessages(
        internal val help: Message = getMessage("help"),
        internal val noBots: Message = getMessage("noBots")
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