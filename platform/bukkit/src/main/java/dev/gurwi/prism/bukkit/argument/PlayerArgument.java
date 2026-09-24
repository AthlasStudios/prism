package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerArgument extends BukkitArgument<PlayerArgument, Player> {

    public PlayerArgument(String name) {
        super(name);

        replaceSuggestions(Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .toList()
        );
    }

    @Override
    public Player parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        Player player = Bukkit.getPlayerExact(value);

        if (player == null) {
            throw new ArgumentParseException(this, value, "Player not found: " + value);
        }

        return player;
    }

}
