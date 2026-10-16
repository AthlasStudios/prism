package dev.gurwi.prism.bukkit.argument.parser;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RelativeValues {

    private RelativeValues() {
    }

    public static double parse(@NotNull String token, double origin) {
        if (token.startsWith("~")) {
            String rest = token.substring(1);
            return origin + (rest.isEmpty() ? 0 : Double.parseDouble(rest));
        }

        return Double.parseDouble(token);
    }

    public static @NotNull Location origin(@NotNull CommandSender sender) {
        if (sender instanceof Entity entity) {
            return entity.getLocation();
        }

        if (sender instanceof BlockCommandSender block) {
            return block.getBlock().getLocation();
        }

        return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    }

    public static @NotNull String @NotNull [] tokens(@NotNull String value) {
        return value.trim().split("\\s+");
    }

}