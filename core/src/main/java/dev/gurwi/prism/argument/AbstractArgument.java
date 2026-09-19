package dev.gurwi.prism.argument;

import dev.gurwi.prism.ChainableBuilder;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractArgument<Impl, T, S extends SuggestionSupplier<T, ?>> implements ChainableBuilder<Impl> {

    private final String name;

    private SuggestionSupplier<T, S> suggestions = SuggestionSupplier.empty();

    public AbstractArgument(String name) {
        this.name = name;
    }

    public abstract Class<T> getPrimitiveType();

    public Impl replaceSuggestions(SuggestionSupplier<T, S> suggestions) {
        this.suggestions = suggestions;
        return instance();
    }

    public Impl replaceSuggestions(Collection<T> suggestions) {
        replaceSuggestions(SuggestionSupplier.sync(suggestions));
        return instance();
    }

    public Impl replaceSuggestions(T... suggestions) {
        replaceSuggestions(SuggestionSupplier.sync(suggestions));
        return instance();
    }

    public Impl replaceSuggestions(CompletableFuture<Collection<T>> suggestions) {
        replaceSuggestions(SuggestionSupplier.async(suggestions));
        return instance();
    }


    public String getName() {
        return name;
    }

    public SuggestionSupplier<T, S> getSuggestionsSupplier() {
        return suggestions;
    }

}
