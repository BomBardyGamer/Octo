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