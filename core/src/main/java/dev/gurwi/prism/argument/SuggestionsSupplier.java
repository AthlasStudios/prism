package dev.gurwi.prism.argument;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SuggestionsSupplier {

    // TODO -> ADD TOOLTIPS

    @Contract(pure = true)
    static SuggestionsSupplier.@NotNull Sync<?, ?> empty() {
        return (source, input) -> Collections.emptyList();
    }

    @Contract(pure = true)
    static <T, Source> @NotNull SuggestionsSupplier.Sync<T, Source> of(Collection<T> suggestions) {
        return (source, input) -> suggestions;
    }

    @SafeVarargs
    @Contract(pure = true)
    static <T, Source> @NotNull SuggestionsSupplier.Sync<T, Source> of(T... suggestions) {
        return of(List.of(suggestions));
    }

    boolean isAsync();

    @FunctionalInterface
    interface Sync<T, Source> extends SuggestionsSupplier {

        Collection<T> getSuggestions(Source source, T input);

        default boolean isAsync() {
            return false;
        }

    }

    @FunctionalInterface
    interface Async<T, Source> extends SuggestionsSupplier {

        CompletableFuture<Collection<T>> getSuggestions(Source source, T input);

        @Override
        default boolean isAsync() {
            return true;
        }

    }

}