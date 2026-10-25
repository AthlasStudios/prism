package dev.gurwi.prism.bukkit.argument.suggestion;

import dev.gurwi.prism.argument.suggestion.SuggestionContext;
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

    static @NotNull Sync unlessNative(@NotNull Sync suggestions) {
        return new UnlessNativeSync(suggestions);
    }

    static @NotNull Async unlessNativeAsync(@NotNull Async suggestions) {
        return new UnlessNativeAsync(suggestions);
    }

    static @NotNull CompletableFuture<Collection<ArgumentSuggestion>> resolve(@NotNull BukkitSuggestionsSupplier<?> supplier, @NotNull SuggestionContext<CommandSender> context) {
        return resolveRaw(supplier, context).thenApply(suggestions -> suggestions != null ? suggestions : Collections.emptyList());
    }

    static @NotNull CompletableFuture<Collection<ArgumentSuggestion>> resolveRaw(@NotNull BukkitSuggestionsSupplier<?> supplier, @NotNull SuggestionContext<CommandSender> context) {
        if (supplier instanceof Async) {
            CompletableFuture<Collection<ArgumentSuggestion>> future = ((Async) supplier).getSuggestions(context);

            return future != null ? future : CompletableFuture.completedFuture(Collections.emptyList());
        }

        if (supplier instanceof Sync) {
            return CompletableFuture.completedFuture(((Sync) supplier).getSuggestions(context));
        }

        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    interface UnlessNative {

    }

    final class UnlessNativeSync implements Sync, UnlessNative {

        private final Sync delegate;

        private UnlessNativeSync(Sync delegate) {
            this.delegate = delegate;
        }

        @Override
        public Collection<ArgumentSuggestion> getSuggestions(SuggestionContext<CommandSender> ctx) {
            if (ctx.hasNativeSuggestions()) {
                return null;
            }

            return delegate.getSuggestions(ctx);
        }

    }

    final class UnlessNativeAsync implements Async, UnlessNative {

        private final Async delegate;

        private UnlessNativeAsync(Async delegate) {
            this.delegate = delegate;
        }

        @Override
        public CompletableFuture<Collection<ArgumentSuggestion>> getSuggestions(SuggestionContext<CommandSender> ctx) {
            if (ctx.hasNativeSuggestions()) {
                return CompletableFuture.completedFuture(null);
            }

            return delegate.getSuggestions(ctx);
        }

    }

    @FunctionalInterface
    interface Sync extends BukkitSuggestionsSupplier<Collection<ArgumentSuggestion>> {

    }

    @FunctionalInterface
    interface Async extends BukkitSuggestionsSupplier<CompletableFuture<Collection<ArgumentSuggestion>>> {

    }

}