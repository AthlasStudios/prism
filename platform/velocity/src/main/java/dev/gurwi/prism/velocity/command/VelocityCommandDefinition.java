package dev.gurwi.prism.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.command.AbstractTreeCommandDefinition;
import dev.gurwi.prism.command.builder.CommandChildrenFactory;
import dev.gurwi.prism.requirement.UsageRequirement;
import dev.gurwi.prism.velocity.argument.VelocityArgument;
import dev.gurwi.prism.velocity.executor.VelocityCommandExecutor;
import dev.gurwi.prism.velocity.permission.VelocityPermission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VelocityCommandDefinition extends AbstractTreeCommandDefinition<VelocityCommandDefinition, CommandSource, VelocityCommandExecutor<?>, VelocityArgument<?, ?>, VelocityPermission> {

    protected VelocityCommandDefinition(@Nullable VelocityCommandDefinition parent, String name, List<String> aliases, String description, UsageRequirement<CommandSource> requirement, Set<VelocityPermission> velocityPermissions, List<VelocityArgument<?, ?>> requiredArguments, List<VelocityArgument<?, ?>> optionalArguments, Map<Class<? extends VelocityCommandExecutor<?>>, VelocityCommandExecutor<?>> executors, @NotNull CommandChildrenFactory<VelocityCommandDefinition> childrenFactory, List<AbstractTreeArgument<?, ?, VelocityCommandDefinition, CommandSource, VelocityCommandExecutor<?>, VelocityArgument<?, ?>, VelocityPermission, ?>> argumentTreeBranches) {
        super(parent, name, aliases, description, requirement, velocityPermissions, requiredArguments, optionalArguments, executors, childrenFactory, argumentTreeBranches);
    }

    protected VelocityCommandDefinition(String name, List<String> aliases, String description, UsageRequirement<CommandSource> requirement, Set<VelocityPermission> velocityPermissions, List<VelocityArgument<?, ?>> requiredArguments, List<VelocityArgument<?, ?>> optionalArguments, Map<Class<? extends VelocityCommandExecutor<?>>, VelocityCommandExecutor<?>> executors, CommandChildrenFactory<VelocityCommandDefinition> childrenFactory, List<AbstractTreeArgument<?, ?, VelocityCommandDefinition, CommandSource, VelocityCommandExecutor<?>, VelocityArgument<?, ?>, VelocityPermission, ?>> argumentTreeBranches) {
        super(name, aliases, description, requirement, velocityPermissions, requiredArguments, optionalArguments, executors, childrenFactory, argumentTreeBranches);
    }

    public boolean hasExecutor(CommandSource source) {
        return getExecutors().stream()
                .anyMatch(executor ->
                        executor.getSourceType().isInstance(source)
                );
    }

    @Override
    public boolean canExecute(CommandSource source) {
        return hasExecutor(source) && super.canExecute(source);
    }

}
