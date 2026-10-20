package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ColorArgument extends BukkitArgument<ColorArgument, ChatColor> {

    public ColorArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(ChatColor.values())
                .map(color -> color.name().toLowerCase())
                .collect(Collectors.toUnmodifiableList());

        replaceSuggestions(values);
        withFixedValues(values);
    }

    @Override
    public ChatColor parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return ChatColor.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        throw new ArgumentParseException(this, value, "Invalid color '" + value + "'. Use a name like dark_red, gold, aqua.");
    }

}