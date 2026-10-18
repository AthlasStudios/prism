package dev.gurwi.prism.bukkit.argument.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public interface LiteralValues extends Supplier<List<String>> {

    @Override
    List<String> get();

    static @NotNull LiteralValues of(String... values) {
        Objects.requireNonNull(values, "values");
        List<String> snapshot = snapshot(Arrays.asList(values));
        return () -> snapshot;
    }

    static @NotNull LiteralValues of(Collection<String> values) {
        List<String> snapshot = snapshot(values);
        return () -> snapshot;
    }

    static <E extends Enum<E>> @NotNull LiteralValues ofEnum(Class<E> type) {
        Objects.requireNonNull(type, "type");

        return of(Arrays.stream(type.getEnumConstants())
                .map(constant -> constant.name().toLowerCase(Locale.ROOT))
                .toList());
    }

    private static @NotNull @Unmodifiable List<String> snapshot(Collection<String> input) {
        Objects.requireNonNull(input, "values");
        Set<String> unique = new LinkedHashSet<>();

        for (String value : input) {
            Objects.requireNonNull(value, "null literal value");

            if (value.isEmpty() || value.chars().anyMatch(Character::isWhitespace)) {
                throw new IllegalArgumentException(
                        "Invalid literal value '" + value + "' (empty or contains whitespace)");
            }

            unique.add(value);
        }

        if (unique.isEmpty()) {
            throw new IllegalArgumentException("A literal argument needs at least one value");
        }

        return List.copyOf(unique);
    }

}