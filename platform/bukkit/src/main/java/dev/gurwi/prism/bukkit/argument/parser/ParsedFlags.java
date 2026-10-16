package dev.gurwi.prism.bukkit.argument.parser;

import dev.gurwi.prism.bukkit.argument.type.Flag;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ParsedFlags {

    private static final ParsedFlags EMPTY = new ParsedFlags(Map.of());

    private final Map<String, List<Object>> values;

    public ParsedFlags(@NotNull Map<String, List<Object>> values) {
        this.values = Map.copyOf(values);
    }

    public static @NotNull ParsedFlags empty() {
        return EMPTY;
    }

    public boolean has(@NotNull Flag<?> flag) {
        return has(flag.getName());
    }

    public boolean isSet(@NotNull Flag<Boolean> flag) {
        return isSet(flag.getName());
    }

    public <T> @NotNull Optional<T> get(@NotNull Flag<T> flag) {
        return get(flag.getName());
    }

    public <T> T get(@NotNull Flag<T> flag, T defaultValue) {
        return get(flag.getName(), defaultValue);
    }

    public <T> @NotNull List<T> getAll(@NotNull Flag<T> flag) {
        return getAll(flag.getName());
    }

    public int count(@NotNull Flag<?> flag) {
        return count(flag.getName());
    }

    public boolean has(@NotNull String flagName) {
        return values.containsKey(flagName);
    }

    public boolean isSet(@NotNull String flagName) {
        return has(flagName);
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull Optional<T> get(@NotNull String flagName) {
        List<Object> list = values.get(flagName);
        return list == null || list.isEmpty() ? Optional.empty() : Optional.of((T) list.get(list.size() - 1));
    }

    public <T> T get(@NotNull String flagName, T defaultValue) {
        return this.<T>get(flagName).orElse(defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull List<T> getAll(@NotNull String flagName) {
        List<Object> list = values.get(flagName);
        return list == null ? List.of() : (List<T>) Collections.unmodifiableList(list);
    }

    public int count(@NotNull String flagName) {
        List<Object> list = values.get(flagName);
        return list == null ? 0 : list.size();
    }

}