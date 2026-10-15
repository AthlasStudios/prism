package dev.gurwi.prism.bukkit.argument.type;

import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class Flag<T> {

    private final String name;
    private final @Nullable Character shortName;
    private final @Nullable BukkitArgument<?, T> value;
    private final boolean repeatable;

    private Flag(@NotNull String name, @Nullable Character shortName, @Nullable BukkitArgument<?, T> value, boolean repeatable) {
        if (name.isBlank() || name.chars().anyMatch(Character::isWhitespace) || name.contains("=")) {
            throw new IllegalArgumentException("Invalid flag name: '" + name + "'");
        }

        this.name = name.toLowerCase(Locale.ROOT);
        this.shortName = shortName == null ? null : Character.toLowerCase(shortName);
        this.value = value;
        this.repeatable = repeatable;
    }

    public static @NotNull Flag<Boolean> presence(@NotNull String name) {
        return new Flag<>(name, null, null, false);
    }

    public static @NotNull Flag<Boolean> presence(@NotNull String name, char shortName) {
        return new Flag<>(name, shortName, null, false);
    }

    public static <T> @NotNull Flag<T> valued(@NotNull BukkitArgument<?, T> value) {
        return new Flag<>(value.getName(), null, value, false);
    }

    public static <T> @NotNull Flag<T> valued(@NotNull BukkitArgument<?, T> value, char shortName) {
        return new Flag<>(value.getName(), shortName, value, false);
    }

    public @NotNull Flag<T> repeatable() {
        return new Flag<>(name, shortName, value, true);
    }

    public @NotNull String getName() {
        return name;
    }

    public @Nullable Character getShortName() {
        return shortName;
    }

    public @Nullable BukkitArgument<?, T> getValue() {
        return value;
    }

    public boolean isPresence() {
        return value == null;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public @NotNull String longLiteral() {
        return "--" + name;
    }

    public @Nullable String shortLiteral() {
        return shortName == null ? null : "-" + shortName;
    }

}