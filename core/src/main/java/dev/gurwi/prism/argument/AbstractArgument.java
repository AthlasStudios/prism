package dev.gurwi.prism.argument;

import dev.gurwi.prism.exception.ArgumentParseException;

import java.util.Collection;

public abstract class AbstractArgument<Impl, T> {

    private final String name;

    private SuggestionsSupplier suggestionsSupplier = SuggestionsSupplier.empty();

    public AbstractArgument(String name) {
        this.name = name;
    }

    public Impl replaceSuggestions(SuggestionsSupplier suggestionsSupplier) {
        this.suggestionsSupplier = suggestionsSupplier;
        return self();
    }

    public Impl replaceSuggestions(Collection<T> suggestions) {
        return replaceSuggestions(SuggestionsSupplier.of(suggestions));
    }

    public Impl replaceSuggestions(T... suggestions) {
        return replaceSuggestions(SuggestionsSupplier.of(suggestions));
    }

    public String getName() {
        return name;
    }

    public SuggestionsSupplier getSuggestionsSupplier() {
        return suggestionsSupplier;
    }

    public abstract T parse(String value) throws ArgumentParseException;

    @SuppressWarnings("unchecked")
    public Impl self() {
        return (Impl) this;
    }

}
