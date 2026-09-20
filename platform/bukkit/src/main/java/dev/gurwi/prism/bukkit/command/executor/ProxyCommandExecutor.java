package dev.gurwi.prism.bukkit.command.executor;

import org.bukkit.command.ProxiedCommandSender;

@FunctionalInterface
public interface ProxyCommandExecutor extends BukkitCommandExecutor<ProxiedCommandSender> {

}
