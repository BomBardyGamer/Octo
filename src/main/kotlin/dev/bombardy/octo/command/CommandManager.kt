package dev.bombardy.octo.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * The command manager, used for registering and executing commands.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class CommandManager(jda: JDA,
                     val prefix: String = "!",
                     private val messages: Map<String, String> = MESSAGE_DEFAULTS
) : ListenerAdapter() {

    private val commands = mutableListOf<Command>()

    init {
        jda.addEventListener(this)
    }

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
        GlobalScope.launch {
            val channel = event.channel
            val message = event.message.contentDisplay.split(" ").iterator()

            val first = message.next()

            val commandName = when (first.startsWith(prefix, true)) {
                true -> first.removePrefix(prefix)
                else -> return@launch
            }

            var foundCommand = false
            commands.forEach outer@{ command ->
                if (commandName in command.names) {
                    foundCommand = true
                    val arguments = when (command.optionalArgs) {
                        false -> message.asSequence().toList().takeIf { it.isNotEmpty() }
                                ?: return@outer channel.sendMessage(command.helpMessage).queue()
                        else -> message.asSequence().toList()
                    }

                    if (event.author.isBot && !command.allowBots) {
                        channel.sendMessage(command.noBotsMessage).queue()
                        return@outer
                    }

                    if (arguments.isNotEmpty()) {
                        command.subCommands.forEach inner@{
                            if (arguments[0] in it.names) {
                                if (it.isSynchronous) {
                                    runBlocking(Dispatchers.Main) {
                                        command.execute(event.message, arguments)
                                    }
                                    return@inner
                                }
                                it.execute(event.message, arguments.subList(1, arguments.size))
                                return@inner
                            }
                        }
                    }

                    if (command.isSynchronous) {
                        runBlocking(Dispatchers.Main) {
                            command.execute(event.message, arguments)
                        }
                        return@outer
                    }

                    command.execute(event.message, arguments)
                    return@outer
                }
            }

            if (!foundCommand) channel.sendMessage(messages.getValue("commandNotFound")).queue()
        }
    }

    companion object {
        private val MESSAGE_DEFAULTS = mapOf(
                "commandNotFound" to "We couldn't find the command you requested."
        )
    }
}