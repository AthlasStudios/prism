package dev.gurwi.prism.spigot.executor;

import dev.gurwi.prism.CommandExecutor;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface SpigotCommandExecutor<T extends CommandSender> extends CommandExecutor<T> {

}
