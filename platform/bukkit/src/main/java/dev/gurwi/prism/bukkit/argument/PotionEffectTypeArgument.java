package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class PotionEffectTypeArgument extends BukkitArgument<PotionEffectTypeArgument, PotionEffectType> {

    public PotionEffectTypeArgument(String name) {
        super(name);

        replaceSuggestions(
                BukkitSuggestionsSupplier.of(
                        Arrays.stream(PotionEffectType.values())
                                .filter(Objects::nonNull)
                                .map(type -> type.getName().toLowerCase())
                                .toList()
                )
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public PotionEffectType parse(@NotNull String value) throws ArgumentParseException {
        PotionEffectType type = PotionEffectType.getByName(value.toUpperCase());

        if (type == null) {
            throw new ArgumentParseException(this, value, "Potion effect type not found: " + value);
        }

        return type;
    }

}
