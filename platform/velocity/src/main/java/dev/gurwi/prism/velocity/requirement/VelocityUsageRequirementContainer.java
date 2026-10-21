package dev.gurwi.prism.velocity.requirement;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.gurwi.prism.command.builder.RequirementContainerBuilder;
import dev.gurwi.prism.requirement.UsageRequirement;

public interface VelocityUsageRequirementContainer<Impl> extends RequirementContainerBuilder<Impl, UsageRequirement<CommandSource>> {

    default <Sender extends CommandSource> Impl withRequirement(Class<Sender> type, UsageRequirement<Sender> requirement) {
        return withRequirement(sender ->
                !type.isInstance(sender) || requirement.test(type.cast(sender))
        );
    }

    default Impl withPlayerRequirement(UsageRequirement<Player> requirement) {
        return withRequirement(Player.class, requirement);
    }

    default Impl withConsoleRequirement(UsageRequirement<ConsoleCommandSource> requirement) {
        return withRequirement(ConsoleCommandSource.class, requirement);
    }

}
