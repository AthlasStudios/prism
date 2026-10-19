package dev.gurwi.prism.bukkit.argument.type;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public final class Flag<T> {

    private final String name;
    private final @Nullable String description;
    private final @Nullable Character shortName;
    private final @Nullable BukkitArgument<?, T> value;
    private final boolean repeatable;

    private Flag(@NotNull String name, @Nullable String description, @Nullable Character shortName, @Nullable BukkitArgument<?, T> value, boolean repeatable) {
        if (name.isBlank() || name.chars().anyMatch(Character::isWhitespace) || name.contains("=")) {
            throw new IllegalArgumentException("Invalid flag name: '" + name + "'");
        }

        this.name = name.toLowerCase(Locale.ROOT);
        this.description = description;
        this.shortName = shortName == null ? null : Character.toLowerCase(shortName);
        this.value = value;
        this.repeatable = repeatable;
    }

    public static @NotNull Flag<Boolean> presence(@NotNull String name) {
        return new Flag<>(name, null, null, null, false);
    }

    public static @NotNull Flag<Boolean> presence(@NotNull String name, @Nullable String description) {
        return new Flag<>(name, description, null, null, false);
    }

    public static @NotNull Flag<Boolean> presence(@NotNull String name, char shortName) {
        return new Flag<>(name, null, shortName, null, false);
    }

    public static @NotNull Flag<Boolean> presence(@NotNull String name, char shortName, @Nullable String description) {
        return new Flag<>(name, description, shortName, null, false);
    }

    public static <T> @NotNull Flag<T> valued(@NotNull BukkitArgument<?, T> value) {
        return new Flag<>(value.getName(), null, null, value, false);
    }

    public static <T> @NotNull Flag<T> valued(@NotNull BukkitArgument<?, T> value, @Nullable String description) {
        return new Flag<>(value.getName(), description, null, value, false);
    }

    public static <T> @NotNull Flag<T> valued(@NotNull BukkitArgument<?, T> value, char shortName) {
        return new Flag<>(value.getName(), null, shortName, value, false);
    }

    public static <T> @NotNull Flag<T> valued(@NotNull BukkitArgument<?, T> value, char shortName, @Nullable String description) {
        return new Flag<>(value.getName(), description, shortName, value, false);
    }

    public @NotNull Flag<T> repeatable() {
        return new Flag<>(name, description, shortName, value, true);
    }

    public @NotNull Flag<T> describe(@Nullable String description) {
        return new Flag<>(name, description, shortName, value, repeatable);
    }

    public @NotNull String getName() {
        return name;
    }

    @Contract(pure = true)
    public @NotNull Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Contract(pure = true)
    public @NotNull Optional<Character> getShortName() {
        return Optional.ofNullable(shortName);
    }

    @Contract(pure = true)
    public @NotNull Optional<BukkitArgument<?, T>> getValue() {
        return Optional.ofNullable(value);
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

    @Contract(pure = true)
    public Optional<String> shortLiteral() {
        if (shortName == null) return Optional.empty();
        return Optional.of("-" + shortName);
    }

}