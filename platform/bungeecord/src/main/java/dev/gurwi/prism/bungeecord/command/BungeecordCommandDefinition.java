package dev.gurwi.prism.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.command.AbstractTreeCommandDefinition;
import dev.gurwi.prism.command.builder.CommandChildrenFactory;
import dev.gurwi.prism.requirement.UsageRequirement;
import dev.gurwi.prism.bungeecord.argument.base.BungeecordArgument;
import dev.gurwi.prism.bungeecord.executor.BungeecordCommandExecutor;
import dev.gurwi.prism.bungeecord.permission.BungeecordPermission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BungeecordCommandDefinition extends AbstractTreeCommandDefinition<BungeecordCommandDefinition, CommandSender, BungeecordCommandExecutor<?>, BungeecordArgument<?, ?>, BungeecordPermission> {

    protected BungeecordCommandDefinition(@Nullable BungeecordCommandDefinition parent, String name, List<String> aliases, String description, UsageRequirement<CommandSender> requirement, Set<BungeecordPermission> bungeecordPermissions, List<BungeecordArgument<?, ?>> requiredArguments, List<BungeecordArgument<?, ?>> optionalArguments, Map<Class<? extends BungeecordCommandExecutor<?>>, BungeecordCommandExecutor<?>> executors, @NotNull CommandChildrenFactory<BungeecordCommandDefinition> childrenFactory, List<AbstractTreeArgument<?, ?, BungeecordCommandDefinition, CommandSender, BungeecordCommandExecutor<?>, BungeecordArgument<?, ?>, BungeecordPermission, ?>> argumentTreeBranches) {
        super(parent, name, aliases, description, requirement, bungeecordPermissions, requiredArguments, optionalArguments, executors, childrenFactory, argumentTreeBranches);
    }

    protected BungeecordCommandDefinition(String name, List<String> aliases, String description, UsageRequirement<CommandSender> requirement, Set<BungeecordPermission> bungeecordPermissions, List<BungeecordArgument<?, ?>> requiredArguments, List<BungeecordArgument<?, ?>> optionalArguments, Map<Class<? extends BungeecordCommandExecutor<?>>, BungeecordCommandExecutor<?>> executors, CommandChildrenFactory<BungeecordCommandDefinition> childrenFactory, List<AbstractTreeArgument<?, ?, BungeecordCommandDefinition, CommandSender, BungeecordCommandExecutor<?>, BungeecordArgument<?, ?>, BungeecordPermission, ?>> argumentTreeBranches) {
        super(name, aliases, description, requirement, bungeecordPermissions, requiredArguments, optionalArguments, executors, childrenFactory, argumentTreeBranches);
    }

    public boolean hasExecutor(CommandSender source) {
        return getExecutors().stream()
                .anyMatch(executor ->
                        executor.accepts(source)
                );
    }

    @Override
    public boolean canExecute(CommandSender source) {
        return hasExecutor(source) && super.canExecute(source);
    }

}
