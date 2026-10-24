package dev.gurwi.prism.bungeecord.argument;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public interface ArgumentSuggestion {

    String getSuggestion();

    @Contract("_ -> new")
    static @NotNull ArgumentSuggestion of(String suggestion) {
        return new Simple(suggestion);
    }

    static @NotNull @Unmodifiable Collection<ArgumentSuggestion> of(@NotNull Collection<String> suggestions) {
        return suggestions.stream().map(ArgumentSuggestion::of).collect(Collectors.toUnmodifiableList());
    }

    final class Simple implements ArgumentSuggestion {

        private final String suggestion;

        public Simple(String suggestion) {
            this.suggestion = suggestion;
        }

        @Override
        public String getSuggestion() {
            return suggestion;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Simple)) return false;

            Simple other = (Simple) o;
            return Objects.equals(suggestion, other.suggestion);
        }

        @Override
        public int hashCode() {
            return Objects.hash(suggestion);
        }

        @Override
        public String toString() {
            return "Simple[suggestion=" + suggestion + "]";
        }

    }

}