package dev.bombardy.octo.command

import dev.bombardy.octo.OctoScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * The command manager, used for registering and executing commands.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class CommandManager : ListenerAdapter() {

    protected open val messageHandler = MessageHandler()
    protected open val commands = mutableListOf<Command>()

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
        OctoScope.launch {
            handle(event.channel, event.message)
        }
    }

    fun registerMessage(id: String, message: (Message) -> Unit) = messageHandler.register(id, message)

    abstract suspend fun handle(channel: TextChannel, message: Message)
}