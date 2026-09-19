package dev.gurwi.prism.spigot.executor;

import dev.gurwi.prism.argument.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerCommandExecutor extends SpigotCommandExecutor<Player> {

    void run(Player player, CommandArguments args);

    default Class<Player> getSourceType() {
        return Player.class;
    }

}
