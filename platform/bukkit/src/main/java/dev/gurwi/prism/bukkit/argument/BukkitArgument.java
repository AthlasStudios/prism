package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.bukkit.requirement.BukkitUsageRequirement;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public abstract class BukkitArgument<Impl, T> extends AbstractArgument<Impl, T, CommandSender, BukkitSuggestionsSupplier> {

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

}
