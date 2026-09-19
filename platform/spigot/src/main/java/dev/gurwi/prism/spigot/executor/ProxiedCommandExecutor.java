package dev.gurwi.prism.spigot.executor;

import dev.gurwi.prism.argument.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;

@FunctionalInterface
public interface ProxiedCommandExecutor extends SpigotCommandExecutor<ProxiedCommandSender> {

    @Override
    void run(ProxiedCommandSender proxy, CommandArguments args);

    @Override
    default Class<ProxiedCommandSender> getSourceType() {
        return ProxiedCommandSender.class;
    }

}
