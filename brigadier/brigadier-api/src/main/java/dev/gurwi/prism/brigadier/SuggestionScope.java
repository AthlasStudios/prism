package dev.gurwi.prism.brigadier;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class SuggestionScope<Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>> {

    private final Definition definition;
    private final List<AbstractArgument<?, ?, ?, ?>> ancestors;
    private final Map<AbstractArgument<?, ?, ?, ?>, String> forced;

    public SuggestionScope(@NotNull Definition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> ancestors, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        this.definition = definition;
        this.ancestors = List.copyOf(ancestors);
        this.forced = forced;
    }

    public @NotNull Definition definition() {
        return definition;
    }

    public @NotNull List<AbstractArgument<?, ?, ?, ?>> ancestors() {
        return ancestors;
    }

    public @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced() {
        return forced;
    }

}
