# Octo
The simple yet responsive JDA command framework, written in Kotlin.

Octo was originally designed to fix the main issue I had with the previous framework
I was using, [Matt's Framework for JDA](https://github.com/ipsk/MattFrameworkJDA), in
that because it is an annotation-based framework, abstraction is near on impossible.
Octo is the command framework that powers [BardyBot](https://github.com/BomBardyGamer/BardyBot).

## Download
Octo is available on my organisation's Repository. Current version can be identified by this badge:
[![Octo](https://img.shields.io/nexus/r/dev.bombardy/octo?label=Octo&server=https%3A%2F%2Frepo.prevarinite.com)](https://repo.prevarinite.com/repository/maven-releases/)

You can depend on it like this:

**Gradle:**
```groovy
dependencies {
    implementation 'dev.bombardy:octo:VERSION'
}
```
```groovy
repositories {
    maven { url 'https://repo.prevarinite.com/repository/maven-public/' }
}
```

**Maven:**
```xml
<dependency>
    <groupId>dev.bombardy</groupId>
    <artifactId>octo</artifactId>
    <version>VERSION</version>
</dependency>
```
```xml
<repository>
    <id>prevarinite</id>
    <url>https://repo.prevarinite.com/repository/maven-public/</url>
</repository>
```

## Usage

The usage for Octo is quite simple, it just requires that all commands extend the abstract `Command` class, like this:

```kotlin
class MyCommand : Command(name = "mycommand") {

    /** Optional override */
    override val options = CommandOptions(aliases = listOf("command", "cmd"))

    override suspend fun execute(message: Message, arguments: List<String>) {
        message.channel.sendMessage("This is my command!").queue()
    }
}
```

Then you can create a new instance of the `DefaultCommandManager` to register your commands using default
command handling, like this:

```kotlin
fun main() {
    val jda = JDABuilder.create(MY_GATEWAY_INTENTS, MY_BOT_TOKEN).build()

    val commandManager = DefaultCommandManager(jda, prefix = "!")
    commandManager.register(MyCommand())
}
```

Also, you are now able to create your own implementation of the `CommandManager` if you require different
functionality other than what the `DefaultCommandManager` provides (such as per-guild prefixes stored in
a database)

```kotlin
class CustomCommandManager : CommandManager() {

    override suspend fun handle(channel: TextChannel, message: Message) {
        // handle commands here
    }
}

fun main() {
    val jda = JDABuilder.create(MY_GATEWAY_INTENTS, MY_BOT_TOKEN).build()

    val commandManager = CustomCommandManager(jda)
    commandManager.register(MyCommand())
}
```