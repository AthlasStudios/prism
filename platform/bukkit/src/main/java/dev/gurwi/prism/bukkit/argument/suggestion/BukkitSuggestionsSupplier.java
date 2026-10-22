package dev.gurwi.prism.bukkit.argument.suggestion;

import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public interface BukkitSuggestionsSupplier<Result> extends SuggestionsSupplier<CommandSender, Result> {

    @Contract(pure = true)
    static @NotNull Sync sync(Sync suggestions) {
        return suggestions;
    }

    @Contract(pure = true)
    static @NotNull Sync sync(Collection<ArgumentSuggestion> suggestions) {
        return ctx -> suggestions;
    }

    @Contract(pure = true)
    static @NotNull Sync strings(Collection<String> suggestions) {
        return ctx -> ArgumentSuggestion.of(suggestions);
    }

    @Contract(pure = true)
    static @NotNull Async async(Async suggestions) {
        return suggestions;
    }

    static @NotNull Async async(CompletableFuture<Collection<ArgumentSuggestion>> suggestionsFuture) {
        return ctx -> suggestionsFuture;
    }

    static @NotNull Async stringsAsync(CompletableFuture<Collection<String>> suggestionsFuture) {
        return ctx -> suggestionsFuture.thenApply(ArgumentSuggestion::of);
    }

    static @NotNull BukkitSuggestionsSupplier<?> empty() {
        return sync(Collections.emptyList());
    }

    @FunctionalInterface
    interface Sync extends BukkitSuggestionsSupplier<Collection<ArgumentSuggestion>> {

    }

    @FunctionalInterface
    interface Async extends BukkitSuggestionsSupplier<CompletableFuture<Collection<ArgumentSuggestion>>> {

    }

}
