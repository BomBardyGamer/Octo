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
open class MessageHandler {

    private val messages = mutableMapOf<String, (Message) -> Unit>()

    init {
        register("commandNotFound") {
            it.channel.sendMessage("We couldn't find the command you requested.").queue()
        }
    }

    /**
     * Registers a message with the given ID
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