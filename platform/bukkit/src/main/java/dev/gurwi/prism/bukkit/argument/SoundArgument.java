package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SoundArgument extends BukkitArgument<SoundArgument, Sound> {

    public SoundArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(Sound.values())
                .map(sound -> sound.name().toLowerCase())
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
    public Sound parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            return Sound.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ArgumentParseException.message(this, value, "Expected a sound got " + value);
        }
    }

}
