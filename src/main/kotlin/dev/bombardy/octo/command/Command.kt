package dev.bombardy.octo.command

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message

/**
 * The base Command class, extended by all commands registered by Octo.
 *
 * By default, all commands will be executed asynchronously using coroutines.
 * You can disable asynchronous execution by setting [isSynchronous] to true.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Suppress("unused")
abstract class Command(internal val names: List<String>,
                       internal val optionalArgs: Boolean = false,
                       internal val allowBots: Boolean = false,
                       internal val isSynchronous: Boolean = false
) {

    /**
     * The help message, outputted when there were no arguments provided, and arguments
     * are not optional.
     *
     * Is of type [Message] to allow for embeds to be provided
     */
    open val helpMessage = MessageBuilder("Use the command properly and you wouldn't see this").build()

    /**
     * The no bots message, outputted when a bot attempts to execute a command and
     * bots are not allowed to execute this command.
     *
     * Is of type [Message] to allow for embeds to be provided.
     */
    open val noBotsMessage = MessageBuilder("Sorry, bots can't execute commands!").build()

    /**
     * A list of sub commands for this command.
     */
    open val subCommands = listOf<Command>()

    /**
     * The method called to execute the command. This is where you put all of your command code.
     *
     * @param message the original message sent, [Message] provided to give author,
     *                channel, and message in one object.
     * @param arguments the arguments given for the command, will be empty if [optionalArgs]
     *                  is set to true and there are no arguments.
     */
    abstract suspend fun execute(message: Message, arguments: List<String>)
}