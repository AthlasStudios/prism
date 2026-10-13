package dev.gurwi.prism.command;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.builder.CommandChildrenFactory;
import dev.gurwi.prism.permission.PermissionNode;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractCommandDefinition<
        Impl extends AbstractCommandDefinition<Impl, Source, Executor, Argument, Permission>,
        Source,
        Executor extends CommandExecutor<? extends Source, Impl>,
        Argument extends AbstractArgument<?, ?, ? extends Source, ?>,
        Permission extends PermissionNode<?, Source>
> {

    private final Map<String, Impl> children = new HashMap<>();

    private final Map<Class<? extends Executor>, Executor> executors = new HashMap<>();
    private final Set<Permission> permissions = new HashSet<>();

    private final List<Argument> requiredArguments = new ArrayList<>();
    private final List<Argument> optionalArguments = new ArrayList<>();

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final UsageRequirement<Source> requirement;

    private final Impl parent;

    protected AbstractCommandDefinition(@Nullable Impl parent, String name, List<String> aliases, String description, UsageRequirement<Source> requirement, Set<Permission> permissions, List<Argument> requiredArguments, List<Argument> optionalArguments, Map<Class<? extends Executor>, Executor> executors, @NotNull CommandChildrenFactory<Impl> childrenFactory) {
        this.parent = parent;
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.requirement = requirement;

        this.permissions.addAll(permissions);
        this.requiredArguments.addAll(requiredArguments);
        this.optionalArguments.addAll(optionalArguments);
        this.executors.putAll(executors);

        //noinspection unchecked
        this.children.putAll(childrenFactory.apply((Impl) this).stream()
                .collect(Collectors.toMap(
                        AbstractCommandDefinition::getName,
                        Function.identity()
                ))
        );
    }

    protected AbstractCommandDefinition(String name, List<String> aliases, String description, UsageRequirement<Source> requirement, Set<Permission> permissions, List<Argument> requiredArguments, List<Argument> optionalArguments, Map<Class<? extends Executor>, Executor> executors, CommandChildrenFactory<Impl> childrenFactory) {
        this(null, name, aliases, description, requirement, permissions, requiredArguments, optionalArguments, executors, childrenFactory);
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public UsageRequirement<Source> getRequirement() {
        return requirement;
    }

    public @Unmodifiable Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public @Unmodifiable List<Argument> getRequiredArguments() {
        return Collections.unmodifiableList(requiredArguments);
    }

    public @Unmodifiable List<Argument> getOptionalArguments() {
        return Collections.unmodifiableList(optionalArguments);
    }

    public <E extends Executor> Optional<E> getExecutor(@NotNull Class<E> type) {
        return Optional.ofNullable(type.cast(executors.get(type)));
    }

    public @Unmodifiable Set<Executor> getExecutors() {
        return Set.copyOf(executors.values());
    }

    public @Unmodifiable Set<Impl> getChildren() {
        return Set.copyOf(children.values());
    }

    public Optional<Impl> getChild(String name) {
        return Optional.ofNullable(children.get(name));
    }

    public Optional<Impl> getParent() {
        return Optional.ofNullable(parent);
    }

    public boolean canExecute(Source source) {
        return  getRequirement().test(source) && getPermissions().stream()
                .allMatch(permission -> permission.hasPermission(source));
    }

}
