package dev.gurwi.prism.bukkit.requirement;

import dev.gurwi.prism.command.builder.RequirementContainerBuilder;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public interface BukkitRequirementContainerBuilder<Impl> extends RequirementContainerBuilder<Impl, UsageRequirement<CommandSender>> {

    default <Sender extends CommandSender> Impl withRequirement(Class<Sender> type, UsageRequirement<Sender> requirement) {
        return withRequirement(sender ->
                !type.isInstance(sender) || requirement.test(type.cast(sender))
        );
    }

    default Impl withPlayerRequirement(UsageRequirement<Player> requirement) {
        return withRequirement(Player.class, requirement);
    }

    default Impl withConsoleRequirement(UsageRequirement<ConsoleCommandSender> requirement) {
        return withRequirement(ConsoleCommandSender.class, requirement);
    }

    default Impl withRemoteConsoleRequirement(UsageRequirement<RemoteConsoleCommandSender> requirement) {
        return withRequirement(RemoteConsoleCommandSender.class, requirement);
    }

    default Impl withProxyRequirement(UsageRequirement<ProxiedCommandSender> requirement) {
        return withRequirement(ProxiedCommandSender.class, requirement);
    }

    default Impl withBlockRequirement(UsageRequirement<BlockCommandSender> requirement) {
        return withRequirement(BlockCommandSender.class, requirement);
    }

}