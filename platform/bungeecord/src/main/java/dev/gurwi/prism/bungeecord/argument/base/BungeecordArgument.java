package dev.gurwi.prism.bungeecord.argument.base;

import com.mojang.brigadier.arguments.ArgumentType;
import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import dev.gurwi.prism.bungeecord.argument.ArgumentSuggestion;
import dev.gurwi.prism.bungeecord.command.BungeecordCommandDefinition;
import dev.gurwi.prism.bungeecord.executor.BungeecordCommandExecutor;
import dev.gurwi.prism.bungeecord.executor.BungeecordExecutorContainerBuilder;
import dev.gurwi.prism.bungeecord.permission.BungeecordPermission;
import dev.gurwi.prism.bungeecord.requirement.BungeecordUsageRequirementContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class BungeecordArgument<Impl extends BungeecordArgument<Impl, T>, T> extends AbstractTreeArgument<
        Impl,
        T,
        BungeecordCommandDefinition,
        CommandSender,
        BungeecordCommandExecutor<?>,
        BungeecordArgument<?, ?>,
        BungeecordPermission,
        SuggestionsSupplier<CommandSender, CompletableFuture<Collection<ArgumentSuggestion>>>
        > implements BungeecordExecutorContainerBuilder<Impl>, BungeecordUsageRequirementContainer<Impl> {

    private @Nullable LiteralValues fixedValues;

    public BungeecordArgument(String name) {
        super(name);
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
        return replaceSuggestions(ignored -> CompletableFuture.completedFuture(List.of(suggestions)));
    }

    public Impl replaceSuggestions(CompletableFuture<Collection<String>> suggestions) {
        return replaceSuggestions(ctx -> suggestions.thenApply(ArgumentSuggestion::of));
    }

    public Impl replaceSuggestions(Collection<String> suggestions) {
        return replaceSuggestions(ignored -> CompletableFuture.completedFuture(suggestions.stream().map(ArgumentSuggestion::of).collect(Collectors.toUnmodifiableList())));
    }

    public Impl replaceSuggestions(String... suggestions) {
        replaceSuggestions(List.of(suggestions));
        return self();
    }

    public Impl includeSuggestions(ArgumentSuggestion... suggestions) {
        return includeSuggestions(ignored -> CompletableFuture.completedFuture(List.of(suggestions)));
    }

    public Impl includeSuggestions(CompletableFuture<Collection<String>> suggestions) {
        return includeSuggestions(ctx -> suggestions.thenApply(ArgumentSuggestion::of));
    }

    public Impl includeSuggestions(Collection<String> suggestions) {
        return includeSuggestions(ignored -> CompletableFuture.completedFuture(suggestions.stream().map(ArgumentSuggestion::of).collect(Collectors.toUnmodifiableList())));
    }

    public Impl includeSuggestions(String... suggestions) {
        return includeSuggestions(List.of(suggestions));
    }

    public Impl includeSuggestionsUnlessNative(Collection<String> suggestions) {
        Collection<ArgumentSuggestion> mapped = ArgumentSuggestion.of(suggestions);
        Collection<ArgumentSuggestion> none = List.of();

        return includeSuggestions(ctx -> CompletableFuture.completedFuture(ctx.hasNativeSuggestions() ? none : mapped));
    }

    public Impl includeSuggestionsUnlessNative(String... suggestions) {
        return includeSuggestionsUnlessNative(List.of(suggestions));
    }

    public boolean hasExecutor(@NotNull CommandSender source) {
        return executors.values().stream().anyMatch(executor -> executor.accepts(source));
    }

    public Optional<LiteralValues> getFixedValues() {
        return Optional.ofNullable(fixedValues);
    }

    public abstract ArgumentType<?> getArgumentType();

}