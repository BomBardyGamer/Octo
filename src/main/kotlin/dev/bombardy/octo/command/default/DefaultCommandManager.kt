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

package dev.bombardy.octo.command.default

import dev.bombardy.octo.command.CommandManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.sharding.ShardManager

/**
 * The default [CommandManager] implementation, has the same functionality
 * as what the [CommandManager] had in 1.0
 *
 * @author Callum Jay Seabrook
 * @since 2.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class DefaultCommandManager(
        val prefix: String = "!"
) : CommandManager() {

    constructor(shardManager: ShardManager, prefix: String = "!") : this(prefix) {
        shardManager.addEventListener(this)
    }

    constructor(jda: JDA, prefix: String = "!") : this(prefix) {
        jda.addEventListener(this)
    }

    override suspend fun handle(channel: TextChannel, message: Message) {
        val iterator = message.contentDisplay.split(" ").iterator()

        val first = iterator.next()

        val commandName = when (first.startsWith(prefix)) {
            true -> first.removePrefix(prefix)
            else -> return
        }

        var foundCommand = false
        commands.forEach outer@{ command ->
            if (commandName == command.name || commandName in command.options.aliases) {
                foundCommand = true

                val arguments = when (command.options.optionalArgs) {
                    true -> iterator.asSequence().toList().takeIf { it.isNotEmpty() }
                            ?: return@outer channel.sendMessage(command.messages.help).queue()
                    else -> iterator.asSequence().toList()
                }

                if (message.author.isBot && !command.options.allowBots) {
                    channel.sendMessage(command.messages.noBots).queue()
                    return@outer
                }

                if (arguments.isNotEmpty()) {
                    command.subCommands.forEach inner@{
                        if (arguments[0] == it.name || arguments[0] in it.options.aliases) {
                            if (it.options.isSynchronous) {
                                runBlocking(Dispatchers.Main) {
                                    it.execute(message, arguments)
                                }
                                return@inner
                            }

                            it.execute(message, arguments)
                            return@inner
                        }
                    }
                }

                if (command.options.isSynchronous) {
                    runBlocking(Dispatchers.Main) {
                        command.execute(message, arguments)
                    }
                    return@outer
                }

                command.execute(message, arguments)
                return@outer
            }
        }

        if (!foundCommand) messageHandler.sendMessage("commandNotFound", message)
    }
}