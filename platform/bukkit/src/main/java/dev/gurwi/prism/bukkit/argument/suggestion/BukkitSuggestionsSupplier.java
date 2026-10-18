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
    static @NotNull Sync sync(Collection<BukkitSuggestion> suggestions) {
        return ctx -> suggestions;
    }

    @Contract(pure = true)
    static @NotNull Sync strings(Collection<String> suggestions) {
        return ctx -> BukkitSuggestion.of(suggestions);
    }

    @Contract(pure = true)
    static @NotNull Async async(Async suggestions) {
        return suggestions;
    }

    static @NotNull Async async(CompletableFuture<Collection<BukkitSuggestion>> suggestionsFuture) {
        return ctx -> suggestionsFuture;
    }

    static @NotNull Async stringsAsync(CompletableFuture<Collection<String>> suggestionsFuture) {
        return ctx -> suggestionsFuture.thenApply(BukkitSuggestion::of);
    }

    static @NotNull BukkitSuggestionsSupplier<?> empty() {
        return sync(Collections.emptyList());
    }

    @FunctionalInterface
    interface Sync extends BukkitSuggestionsSupplier<Collection<BukkitSuggestion>> {

        @Override
        default boolean isAsync() {
            return false;
        }

    }

    @FunctionalInterface
    interface Async extends BukkitSuggestionsSupplier<CompletableFuture<Collection<BukkitSuggestion>>> {

        @Override
        default boolean isAsync() {
            return true;
        }

    }

}
