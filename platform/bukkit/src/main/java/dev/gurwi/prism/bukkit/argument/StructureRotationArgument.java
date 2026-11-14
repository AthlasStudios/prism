package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StructureRotationArgument extends BukkitArgument<StructureRotationArgument, StructureRotation> {

    public StructureRotationArgument(String name) {
        super(name);

        List<String> values = Arrays.stream(StructureRotation.values())
                .map(rotation -> rotation.name().toLowerCase())
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
    public StructureRotation parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        if (value.equals("180")) {
            return StructureRotation.CLOCKWISE_180;
        }

        try {
            return StructureRotation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ArgumentParseException.message(this, value, "Expected a structure rotation got " + value);
        }
    }

}