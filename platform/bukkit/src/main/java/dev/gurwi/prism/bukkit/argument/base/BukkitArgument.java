package dev.gurwi.prism.bukkit.argument.base;

import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestion;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestionsSupplier;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.bukkit.command.executor.BukkitExecutorContainerBuilder;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.bukkit.requirement.BukkitRequirementContainerBuilder;
import dev.gurwi.prism.bukkit.requirement.BukkitUsageRequirement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class BukkitArgument<Impl extends BukkitArgument<Impl, T>, T> extends AbstractTreeArgument<Impl, T, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission, BukkitSuggestionsSupplier<?>> implements BukkitExecutorContainerBuilder<Impl>, BukkitRequirementContainerBuilder<Impl> {

    // TODO -> FORCE SPECIFIC VALUES IN LEGACY EXECUTOR

    private @Nullable LiteralValues fixedValues;

    public BukkitArgument(String name) {
        super(name, BukkitSuggestionsSupplier.empty());
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


    public Impl replaceSuggestions(Function<SuggestionContext<CommandSender>, Collection<String>> suggestions) {
        return replaceSuggestions(BukkitSuggestionsSupplier.sync(ctx -> BukkitSuggestion.of(suggestions.apply(ctx))));
    }

    public Impl replaceSuggestions(Collection<String> suggestions) {
        return replaceSuggestions(BukkitSuggestionsSupplier.strings(suggestions));
    }

    public Impl replaceSuggestions(String... suggestions) {
        return replaceSuggestions(BukkitSuggestionsSupplier.strings(Arrays.asList(suggestions)));
    }

    public Impl replaceSuggestions(CompletableFuture<Collection<String>> suggestions) {
        return replaceSuggestions(BukkitSuggestionsSupplier.stringsAsync(suggestions));
    }

    public Impl withPermissions(Collection<BukkitPermission> permissions) {
        return withRequirement(BukkitUsageRequirement.permission(permissions));
    }

    public Impl withPermissions(BukkitPermission... permissions) {
        return withRequirement(BukkitUsageRequirement.permission(permissions));
    }

    public Impl withPermissions(String... permissions) {
        return withRequirement(BukkitUsageRequirement.permissions(permissions));
    }

    public boolean hasExecutor(@NotNull CommandSender sender) {
        return executors.values().stream().anyMatch(executor -> executor.getSourceType().isInstance(sender));
    }

    public Optional<LiteralValues> getFixedValues() {
        return Optional.ofNullable(fixedValues);
    }

}