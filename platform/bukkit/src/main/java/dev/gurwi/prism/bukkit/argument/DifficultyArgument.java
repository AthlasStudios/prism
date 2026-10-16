package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Difficulty;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DifficultyArgument extends BukkitArgument<DifficultyArgument, Difficulty> {

    public DifficultyArgument(String name) {
        super(name);

        replaceSuggestions(BukkitSuggestionsSupplier.of(
                Arrays.stream(Difficulty.values())
                        .map(difficulty -> difficulty.name().toLowerCase())
                        .toList()
        ));

        forceSuggestions(true);
    }

    @Override
    public Difficulty parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return Difficulty.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a difficulty got " + value);
        }
    }

}
