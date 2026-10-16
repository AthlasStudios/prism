package dev.gurwi.prism.bukkit.command.executor;

import org.bukkit.command.ConsoleCommandSender;

@FunctionalInterface
public interface ConsoleCommandExecutor extends BukkitCommandExecutor<ConsoleCommandSender> {

    default Class<ConsoleCommandSender> getSourceType() {
        return ConsoleCommandSender.class;
    }

}
