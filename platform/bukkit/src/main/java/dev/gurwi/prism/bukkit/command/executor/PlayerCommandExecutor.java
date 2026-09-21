package dev.gurwi.prism.bukkit.command.executor;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerCommandExecutor extends BukkitCommandExecutor<Player> {

    default Class<Player> getSourceType() {
        return Player.class;
    }

}
