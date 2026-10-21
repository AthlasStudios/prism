package dev.gurwi.prism.bukkit.argument.suggestion;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public interface CommandSuggestion {

    String getSuggestion();

    default Optional<String> getTooltip() {
        return Optional.empty();
    }

    @Contract("_ -> new")
    static @NotNull CommandSuggestion of(String suggestion) {
        return new Simple(suggestion, null);
    }

    @Contract("_, _ -> new")
    static @NotNull CommandSuggestion of(String suggestion, @Nullable String tooltip) {
        return new Simple(suggestion, tooltip);
    }

    static @NotNull @Unmodifiable Collection<CommandSuggestion> of(@NotNull Collection<String> suggestions) {
        return suggestions.stream().map(CommandSuggestion::of).collect(Collectors.toUnmodifiableList());
    }

    final class Simple implements CommandSuggestion {

        private final String suggestion;
        private final @Nullable String tooltip;

        public Simple(String suggestion, @Nullable String tooltip) {
            this.suggestion = suggestion;
            this.tooltip = tooltip;
        }

        @Override
        public String getSuggestion() {
            return suggestion;
        }

        @Contract(pure = true)
        @Override
        public @NotNull Optional<String> getTooltip() {
            return Optional.ofNullable(tooltip);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Simple)) return false;
            Simple other = (Simple) o;
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