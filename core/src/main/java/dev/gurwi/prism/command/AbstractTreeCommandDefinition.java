package dev.gurwi.prism.command;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.command.builder.CommandChildrenFactory;
import dev.gurwi.prism.permission.PermissionNode;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractTreeCommandDefinition <
        Impl extends AbstractCommandDefinition<Impl, Source, Executor, Argument, Permission>,
        Source,
        Executor extends CommandExecutor<? extends Source, Impl>,
        Argument extends AbstractArgument<?, ?, ? extends Source, ?>,
        Permission extends PermissionNode<?, Source>
> extends AbstractCommandDefinition<Impl, Source, Executor, Argument, Permission> {

    protected final List<AbstractTreeArgument<?, ?, Impl, Source, Executor, Argument, Permission, ?>> argumentTreeBranches = new ArrayList<>();

    protected AbstractTreeCommandDefinition(@Nullable Impl parent, String name, List<String> aliases, String description, UsageRequirement<Source> requirement, Set<Permission> permissions, List<Argument> requiredArguments, List<Argument> optionalArguments, Map<Class<? extends Executor>, Executor> executors, @NotNull CommandChildrenFactory<Impl> childrenFactory, List<AbstractTreeArgument<?, ?, Impl, Source, Executor, Argument, Permission, ?>> argumentTreeBranches) {
        super(parent, name, aliases, description, requirement, permissions, requiredArguments, optionalArguments, executors, childrenFactory);

        this.argumentTreeBranches.addAll(argumentTreeBranches);
    }

    protected AbstractTreeCommandDefinition(String name, List<String> aliases, String description, UsageRequirement<Source> requirement, Set<Permission> permissions, List<Argument> requiredArguments, List<Argument> optionalArguments, Map<Class<? extends Executor>, Executor> executors, CommandChildrenFactory<Impl> childrenFactory, List<AbstractTreeArgument<?, ?, Impl, Source, Executor, Argument, Permission, ?>> argumentTreeBranches) {
        this(null, name, aliases, description, requirement, permissions, requiredArguments, optionalArguments, executors, childrenFactory, argumentTreeBranches);
    }

    public List<AbstractTreeArgument<?, ?, Impl, Source, Executor, Argument, Permission, ?>> getArgumentTreeBranches() {
        return Collections.unmodifiableList(argumentTreeBranches);
    }

}
