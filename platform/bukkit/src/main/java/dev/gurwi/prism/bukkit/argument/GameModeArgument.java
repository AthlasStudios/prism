package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GameModeArgument extends BukkitArgument<GameModeArgument, GameMode> {

    public GameModeArgument(String name) {
        super(name);

        replaceSuggestions(
                BukkitSuggestionsSupplier.of(
                        Arrays.stream(GameMode.values())
                                .map(gameMode -> gameMode.name().toLowerCase())
                                .toList()
                )
        );
    }

    @Override
    public GameMode parse(@NotNull String value) throws ArgumentParseException {
        try {
            return GameMode.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a game mode got " + value);
        }
    }

}
