package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class OfflinePlayerArgument extends BukkitArgument<OfflinePlayerArgument, OfflinePlayer> {

    public OfflinePlayerArgument(String name) {
        super(name);

        replaceSuggestions(Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toUnmodifiableList())
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public OfflinePlayer parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(value);

        if (!player.hasPlayedBefore() && !player.isOnline()) {
            throw new ArgumentParseException(this, value, "Offline player not found: " + value);
        }

        return player;
    }

}
