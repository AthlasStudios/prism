package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SoundArgument extends BukkitArgument<SoundArgument, Sound> {

    public SoundArgument(String name) {
        super(name);

        replaceSuggestions(
                BukkitSuggestionsSupplier.of(Arrays.stream(Sound.values())
                        .map(sound -> sound.name().toLowerCase())
                        .toList())
        );
    }

    @Override
    public Sound parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return Sound.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a sound got " + value);
        }
    }

}
