package dev.gurwi.prism.bukkit.command.executor;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface GenericCommandExecutor extends BukkitCommandExecutor<CommandSender> {

    default Class<CommandSender> getSourceType() {
        return CommandSender.class;
    }

}
