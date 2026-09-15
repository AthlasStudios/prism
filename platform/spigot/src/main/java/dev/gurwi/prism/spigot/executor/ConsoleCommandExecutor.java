package dev.gurwi.prism.spigot.executor;

import dev.gurwi.prism.argument.CommandArguments;
import org.bukkit.command.ConsoleCommandSender;

@FunctionalInterface
public interface ConsoleCommandExecutor extends SpigotCommandExecutor<ConsoleCommandSender> {

    void run(ConsoleCommandSender sender, CommandArguments args);

}
