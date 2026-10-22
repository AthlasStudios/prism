package dev.gurwi.prism.velocity.argument.base;

import com.mojang.brigadier.arguments.ArgumentType;
import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import dev.gurwi.prism.velocity.argument.ArgumentSuggestion;
import dev.gurwi.prism.velocity.command.VelocityCommandDefinition;
import dev.gurwi.prism.velocity.executor.VelocityCommandExecutor;
import dev.gurwi.prism.velocity.executor.VelocityExecutorContainerBuilder;
import dev.gurwi.prism.velocity.permission.VelocityPermission;
import dev.gurwi.prism.velocity.requirement.VelocityUsageRequirementContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class VelocityArgument<Impl extends VelocityArgument<Impl, T>, T> extends AbstractTreeArgument<
        Impl,
        T,
        VelocityCommandDefinition,
        CommandSource,
        VelocityCommandExecutor<?>,
        VelocityArgument<?, ?>,
        VelocityPermission,
        SuggestionsSupplier<CommandSource, CompletableFuture<Collection<ArgumentSuggestion>>>
> implements VelocityExecutorContainerBuilder<Impl>, VelocityUsageRequirementContainer<Impl> {

    private @Nullable LiteralValues fixedValues;

    public VelocityArgument(String name) {
        super(name, SuggestionsSupplier.empty());
    }

    public Impl withFixedValues(LiteralValues values) {
        this.fixedValues = Objects.requireNonNull(values, "values");
        return self();
    }

    public Impl withFixedValues(String... values) {
        return withFixedValues(LiteralValues.of(values));
    }

    public Impl withFixedValues(Collection<String> values) {
        return withFixedValues(LiteralValues.of(values));
    }

    public Impl replaceSuggestions(ArgumentSuggestion... suggestions) {
        return replaceSuggestions(_ -> CompletableFuture.completedFuture(List.of(suggestions)));
    }

    public Impl replaceSuggestions(CompletableFuture<Collection<String>> suggestions) {
        return replaceSuggestions(ctx -> suggestions.thenApply(ArgumentSuggestion::of));
    }

    public Impl replaceSuggestions(Collection<String> suggestions) {
        return replaceSuggestions(_ -> CompletableFuture.completedFuture(suggestions.stream().map(ArgumentSuggestion::of).toList()));
    }

    public Impl replaceSuggestions(String... suggestions) {
        replaceSuggestions(List.of(suggestions));
        return self();
    }

    public boolean hasExecutor(@NotNull CommandSource source) {
        return executors.values().stream().anyMatch(executor -> executor.getSourceType().isInstance(source));
    }

    public Optional<LiteralValues> getFixedValues() {
        return Optional.ofNullable(fixedValues);
    }

    public abstract ArgumentType<?> getArgumentType();

}
