package dev.gurwi.prism.bungeecord.executor;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ConsoleCommandExecutor extends BungeecordCommandExecutor<CommandSender> {

    @Override
    default Class<CommandSender> getSourceType() {
        return CommandSender.class;
    }

    @Override
    default boolean accepts(@NotNull CommandSender sender) {
        return !(sender instanceof ProxiedPlayer);
    }

}
