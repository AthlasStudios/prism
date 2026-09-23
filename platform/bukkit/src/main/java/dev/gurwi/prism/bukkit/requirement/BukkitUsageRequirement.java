package dev.gurwi.prism.bukkit.requirement;

import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.bukkit.permission.CommandPermission;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@FunctionalInterface
public interface BukkitUsageRequirement extends UsageRequirement<CommandSender> {

    @Contract(pure = true)
    static @NotNull BukkitUsageRequirement permission(Collection<BukkitPermission> permissions) {
        return sender -> permissions.stream().allMatch(permission -> permission.hasPermission(sender));
    }

    static @NotNull BukkitUsageRequirement permission(BukkitPermission... permissions) {
        return permission(Set.of(permissions));
    }

    static @NotNull BukkitUsageRequirement permissions(String... permissions) {
        return permission(BukkitPermission.of(permissions));
    }

    static @NotNull BukkitUsageRequirement isOp() {
        return CommandPermission.OP::hasPermission;
    }

}
