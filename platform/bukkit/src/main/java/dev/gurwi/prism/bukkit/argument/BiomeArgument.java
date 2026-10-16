package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BiomeArgument extends BukkitArgument<BiomeArgument, Biome> {

    public BiomeArgument(String name) {
        super(name);

        replaceSuggestions(
                Arrays.stream(Biome.values())
                        .map(biome -> biome.name().toLowerCase())
                        .toList()
        );

        forceSuggestions(true);
    }

    @Override
    public Biome parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return Biome.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected a biome got " + value);
        }
    }

}
