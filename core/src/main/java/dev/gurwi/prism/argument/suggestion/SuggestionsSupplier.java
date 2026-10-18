package dev.gurwi.prism.argument.suggestion;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface SuggestionsSupplier<Source, Result> {

    Result getSuggestions(SuggestionContext<Source> ctx);

    boolean isAsync();

    @FunctionalInterface
    interface Sync<T, Source> extends SuggestionsSupplier<Source, Collection<T>> {

        default boolean isAsync() {
            return false;
        }

    }

    @FunctionalInterface
    interface Async<T, Source> extends SuggestionsSupplier<Source, CompletableFuture<Collection<T>>> {

        @Override
        default boolean isAsync() {
            return true;
        }

    }

}