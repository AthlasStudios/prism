package dev.gurwi.prism.argument;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SuggestionsSupplier {

    @Contract(pure = true)
    static @NotNull SuggestionsSupplier empty() {
        return (Sync<Object, Object>) o -> Collections.emptyList();
    }

    @Contract(pure = true)
    static <T, Source> @NotNull SuggestionsSupplier.Sync<T, Source> of(Collection<T> suggestions) {
        return source -> suggestions;
    }

    @SafeVarargs
    @Contract(pure = true)
    static <T, Source> @NotNull SuggestionsSupplier.Sync<T, Source> of(T... suggestions) {
        return of(List.of(suggestions));
    }

    default boolean isAsync() {
        return this instanceof Async;
    }

    @FunctionalInterface
    interface Sync<T, Source> extends SuggestionsSupplier {

        Collection<T> getSuggestions(Source source);

    }

    @FunctionalInterface
    interface Async<T, Source> extends SuggestionsSupplier {

        CompletableFuture<Collection<T>> getSuggestions(Source source);

    }

}
