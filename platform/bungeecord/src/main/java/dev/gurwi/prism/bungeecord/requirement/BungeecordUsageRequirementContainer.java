package dev.gurwi.prism.bungeecord.requirement;

import dev.gurwi.prism.command.builder.RequirementContainerBuilder;
import dev.gurwi.prism.requirement.UsageRequirement;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface BungeecordUsageRequirementContainer<Impl> extends RequirementContainerBuilder<Impl, UsageRequirement<CommandSender>> {

    default <Sender extends CommandSender> Impl withRequirement(Class<Sender> type, UsageRequirement<Sender> requirement) {
        return withRequirement(sender ->
                !type.isInstance(sender) || requirement.test(type.cast(sender))
        );
    }

    default Impl withPlayerRequirement(UsageRequirement<ProxiedPlayer> requirement) {
        return withRequirement(ProxiedPlayer.class, requirement);
    }

    default Impl withConsoleRequirement(UsageRequirement<CommandSender> requirement) {
        return withRequirement(sender ->
                sender instanceof ProxiedPlayer || requirement.test(sender)
        );
    }

}
