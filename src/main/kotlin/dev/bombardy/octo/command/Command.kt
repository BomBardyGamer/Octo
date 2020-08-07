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

package dev.bombardy.octo.command

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message

/**
 * The base Command class, extended by all commands registered by Octo.
 *
 * By default, all commands will be executed asynchronously using coroutines.
 * You can disable asynchronous execution by setting the isSynchronous option
 * to true.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Suppress("unused")
abstract class Command(
        internal val name: String
) {

    /**
     * The options for this command
     */
    open val options = CommandOptions()

    /**
     * The messages for this command
     */
    open val messages = CommandMessages()

    /**
     * The command's sub commands
     */
    open val subCommands = listOf<Command>()

    /**
     * The method called to execute the command. This is where you put all of your command code.
     *
     * @param message the original message sent, [Message] provided to give author,
     *                channel, and message in one object.
     * @param arguments the arguments given for the command, will be empty if the optionalArgs
     *                  option is set to true and there are no arguments.
     */
    abstract suspend fun execute(message: Message, arguments: List<String>)
}