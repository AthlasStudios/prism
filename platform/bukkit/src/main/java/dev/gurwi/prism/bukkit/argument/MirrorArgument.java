package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.block.structure.Mirror;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MirrorArgument extends BukkitArgument<MirrorArgument, Mirror> {

    public MirrorArgument(String name) {
        super(name);

        replaceSuggestions(
                BukkitSuggestionsSupplier.of(
                        Arrays.stream(Mirror.values())
                                .map(mirror -> mirror.name().toLowerCase())
                                .toList()
                )
        );

        forceSuggestions(true);
    }

    @Override
    public Mirror parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return Mirror.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a mirror got " + value);
        }
    }

}