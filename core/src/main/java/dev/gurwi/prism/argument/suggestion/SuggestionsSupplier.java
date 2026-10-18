package dev.gurwi.prism.argument.suggestion;

public interface SuggestionsSupplier<Source, Result> {

    Result getSuggestions(SuggestionContext<Source> ctx);

}