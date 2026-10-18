package dev.gurwi.prism.bukkit.argument.suggestion;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public interface BukkitSuggestion {

    String getSuggestion();

    default @Nullable String getTooltip() {
        return null;
    }

    @Contract("_ -> new")
    static @NotNull BukkitSuggestion of(String suggestion) {
        return new Simple(suggestion, null);
    }

    @Contract("_, _ -> new")
    static @NotNull BukkitSuggestion of(String suggestion, @Nullable String tooltip) {
        return new Simple(suggestion, tooltip);
    }

    static @NotNull @Unmodifiable Collection<BukkitSuggestion> of(@NotNull Collection<String> suggestions) {
        return suggestions.stream().map(BukkitSuggestion::of).toList();
    }

    record Simple(String suggestion, @Nullable String tooltip) implements BukkitSuggestion {
        @Override public String getSuggestion() { return suggestion; }
        @Override public @Nullable String getTooltip() { return tooltip; }
    }

}
