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

package dev.bombardy.octo.command.default

import dev.bombardy.octo.command.CommandManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.sharding.ShardManager

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