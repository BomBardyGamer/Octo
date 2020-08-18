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

import dev.bombardy.octo.OctoScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.sharding.ShardManager

/**
 * The base command manager, made abstract to allow for custom command handling
 *
 * To create your own command handler method, inherit from this class and override
 * the [handle] method
 *
 * If you're looking for what this used to do, see the [dev.bombardy.octo.command.default.DefaultCommandManager]
 *
 * @author Callum Seabrook
 * @since 1.0
 * @see [dev.bombardy.octo.command.default.DefaultCommandManager]
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class CommandManager() : ListenerAdapter() {

    constructor(shardManager: ShardManager) : this() {
        shardManager.addEventListener(this)
    }

    constructor(jda: JDA) : this() {
        jda.addEventListener(this)
    }

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
    fun registerAll(commands: Iterable<Command>) = commands.forEach(this::register)

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        OctoScope.launch {
            handle(event.channel, event.message)
        }
    }

    fun registerMessage(id: String, message: (Message) -> Unit) = messageHandler.register(id, message)

    abstract suspend fun handle(channel: TextChannel, message: Message)
}