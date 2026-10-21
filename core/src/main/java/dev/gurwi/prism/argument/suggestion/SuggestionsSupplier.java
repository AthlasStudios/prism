package dev.gurwi.prism.argument.suggestion;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SuggestionsSupplier<Source, Result> {

    Result getSuggestions(SuggestionContext<Source> ctx);

    @Contract(pure = true)
    static <Source, Result> @NotNull SuggestionsSupplier<Source, Result> empty() {
        return ctx -> null;
    }

}