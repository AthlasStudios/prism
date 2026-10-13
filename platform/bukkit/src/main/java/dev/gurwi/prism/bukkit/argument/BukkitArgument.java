package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.bukkit.command.executor.BukkitExecutorContainerBuilder;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.bukkit.requirement.BukkitRequirementContainerBuilder;
import dev.gurwi.prism.bukkit.requirement.BukkitUsageRequirement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class BukkitArgument<Impl extends BukkitArgument<Impl, T>, T> extends AbstractTreeArgument<Impl, T, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission, BukkitSuggestionsSupplier> implements BukkitExecutorContainerBuilder<Impl>, BukkitRequirementContainerBuilder<Impl> {

    // TODO -> FORCE SPECIFIC VALUES

    public BukkitArgument(String name) {
        super(name, BukkitSuggestionsSupplier.empty());
    }

    public Impl replaceSuggestions(Collection<String> suggestions) {
        return replaceSuggestions(BukkitSuggestionsSupplier.of(suggestions));
    }

    public Impl replaceSuggestions(String... suggestions) {
        return replaceSuggestions(BukkitSuggestionsSupplier.of(suggestions));
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

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return super.canUse(sender) && hasExecutor(sender);
    }

}
