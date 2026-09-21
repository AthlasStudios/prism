package dev.gurwi.prism.argument;

import dev.gurwi.prism.exception.ArgumentParseException;

import java.util.Collection;

@SuppressWarnings("unchecked")
public abstract class AbstractArgument<Impl, T, Suggestions extends SuggestionsSupplier> {

    private final String name;

    private Suggestions suggestionsSupplier = (Suggestions) SuggestionsSupplier.empty();

    public AbstractArgument(String name) {
        this.name = name;
    }

    public Impl replaceSuggestions(Suggestions suggestionsSupplier) {
        this.suggestionsSupplier = suggestionsSupplier;
        return self();
    }

    public Impl replaceSuggestions(Collection<T> suggestions) {
        return replaceSuggestions((Suggestions) SuggestionsSupplier.of(suggestions));
    }

    public Impl replaceSuggestions(T... suggestions) {
        return replaceSuggestions((Suggestions) SuggestionsSupplier.of(suggestions));
    }

    public String getName() {
        return name;
    }

    public Suggestions getSuggestionsSupplier() {
        return suggestionsSupplier;
    }

    public abstract T parse(String value) throws ArgumentParseException;

    @SuppressWarnings("unchecked")
    public Impl self() {
        return (Impl) this;
    }

}
