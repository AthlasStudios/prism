package dev.gurwi.prism.argument;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface SuggestionSupplier<T, S> {

    CompletableFuture<Collection<T>> get(S source);

    @Contract(pure = true)
    static <T, S> @NotNull SuggestionSupplier<T, S> sync(Collection<T> suggestions) {
        return source -> CompletableFuture.completedFuture(suggestions);
    }

    @SafeVarargs
    static <T, S> @NotNull SuggestionSupplier<T, S> sync(T... suggestions) {
        return sync(List.of(suggestions));
    }

    static <T, S> @NotNull SuggestionSupplier<T, S> async(CompletableFuture<Collection<T>> suggestions) {
        return source -> suggestions;
    }

    static <T, S> @NotNull SuggestionSupplier<T, S> empty() {
        return sync(Collections.emptyList());
    }

}
