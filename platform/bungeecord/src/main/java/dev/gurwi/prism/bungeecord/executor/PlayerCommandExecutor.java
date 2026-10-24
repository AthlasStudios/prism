package dev.gurwi.prism.bungeecord.executor;

import net.md_5.bungee.api.connection.ProxiedPlayer;

@FunctionalInterface
public interface PlayerCommandExecutor extends BungeecordCommandExecutor<ProxiedPlayer> {

    @Override
    default Class<ProxiedPlayer> getSourceType() {
        return ProxiedPlayer.class;
    }

}
