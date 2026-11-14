package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BiomeArgument extends BukkitArgument<BiomeArgument, Biome> {

    public BiomeArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(Biome.values())
                .map(biome -> biome.name().toLowerCase())
                .collect(Collectors.toUnmodifiableList());

        whenBrigadier(ctx -> {
            if (!ctx.hasNativeType() && ctx.getArgument().getFixedValues().isEmpty()) {
                ctx.getArgument().withFixedValues(values);
            }

            if (!ctx.hasNativeSuggestions()) {
                ctx.getArgument().includeSuggestions(values);
            }
        });

        whenLegacy(ctx -> {
            if (ctx.getArgument().getFixedValues().isEmpty()) {
                ctx.getArgument().withFixedValues(values);
            }

            ctx.getArgument().includeSuggestions(values);
        });
    }

    @Override
    public Biome parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return Biome.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ArgumentParseException.message(this, value, "Expected a biome got " + value);
        }
    }

}
