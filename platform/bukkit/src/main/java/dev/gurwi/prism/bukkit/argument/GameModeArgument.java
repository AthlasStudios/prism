package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameModeArgument extends BukkitArgument<GameModeArgument, GameMode> {

    public GameModeArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(GameMode.values())
                .map(gameMode -> gameMode.name().toLowerCase())
                .collect(Collectors.toUnmodifiableList());

        replaceSuggestions(values);
        withFixedValues(values);
    }

    @Override
    public GameMode parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return GameMode.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a game mode got " + value);
        }
    }

}
