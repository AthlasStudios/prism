package dev.gurwi.prism.bukkit.command.executor;

import dev.gurwi.prism.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface BukkitCommandExecutor<Sender extends CommandSender> extends CommandExecutor<Sender> {

}
