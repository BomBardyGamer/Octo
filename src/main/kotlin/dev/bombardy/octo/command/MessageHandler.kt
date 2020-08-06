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