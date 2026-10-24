package dev.gurwi.prism.argument;

import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface ArgumentGroup {

    @SafeVarargs
    @Contract("_, _ -> param2")
    static <Source, T extends AbstractArgument<?, ?, Source, ?>> T[] requirement(UsageRequirement<Source> requirement, T @NotNull ... args) {
        for (T arg : args) {
            arg.withRequirement(requirement);
        }

        return args;
    }

    @SafeVarargs
    @Contract("_, _ -> param2")
    static <Source, Suggestions extends SuggestionsSupplier<Source, ?>, T extends AbstractArgument<?, ?, Source, Suggestions>> T[] suggestions(Suggestions supplier, T @NotNull ... args) {
        for (T arg : args) {
            arg.replaceSuggestions(supplier);
        }

        return args;
    }

    @SafeVarargs
    @Contract("_, _ -> param2")
    static <Source, Suggestions extends SuggestionsSupplier<Source, ?>, T extends AbstractArgument<?, ?, Source, Suggestions>> T[] includeSuggestions(Suggestions supplier, T @NotNull ... args) {
        for (T arg : args) {
            arg.includeSuggestions(supplier);
        }

        return args;
    }

}