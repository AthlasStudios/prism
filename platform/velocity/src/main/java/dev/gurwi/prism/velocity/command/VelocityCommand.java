package dev.gurwi.prism.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.command.builder.AbstractTreeCommandBuilder;
import dev.gurwi.prism.velocity.argument.base.VelocityArgument;
import dev.gurwi.prism.velocity.executor.VelocityCommandExecutor;
import dev.gurwi.prism.velocity.executor.VelocityExecutorContainerBuilder;
import dev.gurwi.prism.velocity.permission.VelocityPermission;
import dev.gurwi.prism.velocity.requirement.VelocityUsageRequirementContainer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VelocityCommand extends AbstractTreeCommandBuilder<
        VelocityCommand,
        VelocityCommandDefinition,
        CommandSource,
        VelocityCommandExecutor<?>,
        VelocityArgument<?, ?>,
        VelocityPermission
> implements VelocityExecutorContainerBuilder<VelocityCommand>, VelocityUsageRequirementContainer<VelocityCommand> {

    public VelocityCommand(String name, List<String> aliases) {
        super(name, aliases);
    }

    public VelocityCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public VelocityCommandDefinition build(@Nullable VelocityCommandDefinition parent) {
        return null;
    }

}
