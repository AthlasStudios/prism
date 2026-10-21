package dev.gurwi.prism.velocity.executor;

import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.command.CommandExecutor;
import dev.gurwi.prism.velocity.command.VelocityCommandDefinition;
import org.jetbrains.annotations.NotNull;

public interface VelocityCommandExecutor<Source extends CommandSource> extends CommandExecutor<Source, VelocityCommandDefinition> {

    Class<Source> getSourceType();

    default void tryInvoke(@NotNull CommandSource sender, CommandArguments<VelocityCommandDefinition, ? super Source> args) {
        if (!getSourceType().isInstance(sender)) return;
        run(getSourceType().cast(sender), args);
    }

}
