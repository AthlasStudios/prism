package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EntityTypeArgument extends BukkitArgument<EntityTypeArgument, EntityType> {

    public EntityTypeArgument(String name) {
        super(name);

        replaceSuggestions(
                BukkitSuggestionsSupplier.of(
                        Arrays.stream(EntityType.values())
                                .map(entityType -> entityType.name().toLowerCase())
                                .toList()
                )
        );
    }

    @Override
    public EntityType parse(@NotNull String value) throws ArgumentParseException {
        try {
            return EntityType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentParseException(this, value, "Expected an entity type got " + value);
        }
    }

}
