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
        val name: String
) {

    /**
     * The options for this command
     */
    open val options = defaultOptions()

    /**
     * The messages for this command
     */
    open val messages = messagesOf()

    /**
     * The command's sub commands
     */
    open val subCommands: Iterable<Command> = emptySet()

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