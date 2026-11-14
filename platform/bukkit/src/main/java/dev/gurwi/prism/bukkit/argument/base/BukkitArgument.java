package dev.gurwi.prism.bukkit.argument.base;

import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestionsSupplier;
import dev.gurwi.prism.bukkit.argument.suggestion.ArgumentSuggestion;
import dev.gurwi.prism.bukkit.brigadier.BrigadierContext;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.bukkit.command.executor.BukkitExecutorContainerBuilder;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.bukkit.requirement.BukkitRequirementContainerBuilder;
import dev.gurwi.prism.bukkit.requirement.BukkitUsageRequirement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class BukkitArgument<Impl extends BukkitArgument<Impl, T>, T> extends AbstractTreeArgument<Impl, T, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission, BukkitSuggestionsSupplier<?>> implements BukkitExecutorContainerBuilder<Impl>, BukkitRequirementContainerBuilder<Impl> {

    private final List<Consumer<BrigadierContext<Impl>>> brigadierConfigurations = new ArrayList<>();
    private final List<Consumer<ArgumentContext<Impl>>> legacyConfigurations = new ArrayList<>();

    private @Nullable LiteralValues fixedValues;
    private boolean brigadierConfigured;
    private boolean legacyConfigured;

    public BukkitArgument(String name) {
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
        return replaceSuggestions(BukkitSuggestionsSupplier.sync(Arrays.asList(suggestions)));
    }

    public Impl replaceSuggestions(Collection<String> suggestions) {
        if (isBlank(suggestions)) {
            return replaceSuggestions((BukkitSuggestionsSupplier<?>) null);
        }

        return replaceSuggestions(BukkitSuggestionsSupplier.strings(suggestions));
    }

    public Impl replaceSuggestions(String... suggestions) {
        return replaceSuggestions(Arrays.asList(suggestions));
    }

    public Impl replaceSuggestions(CompletableFuture<Collection<String>> suggestions) {
        return replaceSuggestions(BukkitSuggestionsSupplier.stringsAsync(suggestions));
    }

    public Impl includeSuggestions(ArgumentSuggestion... suggestions) {
        return includeSuggestions(BukkitSuggestionsSupplier.sync(Arrays.asList(suggestions)));
    }

    public Impl includeSuggestions(Collection<String> suggestions) {
        if (isBlank(suggestions)) {
            return self();
        }

        return includeSuggestions(BukkitSuggestionsSupplier.strings(suggestions));
    }

    public Impl includeSuggestions(String... suggestions) {
        return includeSuggestions(Arrays.asList(suggestions));
    }

    public Impl includeSuggestions(CompletableFuture<Collection<String>> suggestions) {
        return includeSuggestions(BukkitSuggestionsSupplier.stringsAsync(suggestions));
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

    public Impl whenBrigadier(Consumer<BrigadierContext<Impl>> configuration) {
        this.brigadierConfigurations.add(Objects.requireNonNull(configuration, "configuration"));
        return self();
    }

    public Impl whenLegacy(Consumer<ArgumentContext<Impl>> configuration) {
        this.legacyConfigurations.add(Objects.requireNonNull(configuration, "configuration"));
        return self();
    }

    public synchronized void applyBrigadier(@Nullable Object nativeType, boolean nativeSuggestions) {
        if (brigadierConfigured) {
            return;
        }

        brigadierConfigured = true;

        BrigadierContext<Impl> context = new BrigadierContext<>(self(), nativeType, nativeSuggestions);

        for (Consumer<BrigadierContext<Impl>> configuration : new ArrayList<>(brigadierConfigurations)) {
            configuration.accept(context);
        }
    }

    public synchronized void applyLegacy() {
        if (legacyConfigured) {
            return;
        }

        legacyConfigured = true;

        ArgumentContext<Impl> context = new ArgumentContext<>(self());

        for (Consumer<ArgumentContext<Impl>> configuration : new ArrayList<>(legacyConfigurations)) {
            configuration.accept(context);
        }
    }

    public @NotNull Collection<BukkitArgument<?, ?>> getNestedArguments() {
        return List.of();
    }

    public boolean hasExecutor(@NotNull CommandSender sender) {
        return executors.values().stream().anyMatch(executor -> executor.getSourceType().isInstance(sender));
    }

    public Optional<LiteralValues> getFixedValues() {
        return Optional.ofNullable(fixedValues);
    }

    private static boolean isBlank(Collection<String> suggestions) {
        return suggestions == null || suggestions.stream().allMatch(value -> value == null || value.isEmpty());
    }

}