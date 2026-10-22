package dev.gurwi.prism.velocity.argument;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public interface ArgumentSuggestion {

    String getSuggestion();

    default Optional<Component> getTooltip() {
        return Optional.empty();
    }

    @Contract("_ -> new")
    static @NotNull ArgumentSuggestion of(String suggestion) {
        return new Simple(suggestion, null);
    }

    @Contract("_, _ -> new")
    static @NotNull ArgumentSuggestion of(String suggestion, @Nullable Component tooltip) {
        return new Simple(suggestion, tooltip);
    }

    static @NotNull @Unmodifiable Collection<ArgumentSuggestion> of(@NotNull Collection<String> suggestions) {
        return suggestions.stream().map(ArgumentSuggestion::of).toList();
    }

    final class Simple implements ArgumentSuggestion {

        private final String suggestion;
        private final @Nullable Component tooltip;

        public Simple(String suggestion, @Nullable Component tooltip) {
            this.suggestion = suggestion;
            this.tooltip = tooltip;
        }

        @Override
        public String getSuggestion() {
            return suggestion;
        }

        @Contract(pure = true)
        @Override
        public @NotNull Optional<Component> getTooltip() {
            return Optional.ofNullable(tooltip);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Simple other)) return false;
            return Objects.equals(suggestion, other.suggestion) && Objects.equals(tooltip, other.tooltip);
        }

        @Override
        public int hashCode() {
            return Objects.hash(suggestion, tooltip);
        }

        @Override
        public String toString() {
            return "Simple[suggestion=" + suggestion + ", tooltip=" + tooltip + "]";
        }

    }

}