package dev.gurwi.prism.command;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.permission.PermissionNode;
import dev.gurwi.prism.platform.CommandsRegistry;
import dev.gurwi.prism.platform.Platform;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractCommandBuilder<
        Impl extends AbstractCommandBuilder<Impl, Definition, Source, Executor, Argument, Permission>,
        Definition extends AbstractCommandDefinition<Definition, Source, Executor, Argument, Permission>,
        Source,
        Executor extends CommandExecutor<? extends Source, Definition>,
        Argument extends AbstractArgument<?, ?, Source, ?>,
        Permission extends PermissionNode<?, Source>
> {

    protected final List<Argument> requiredArguments = new ArrayList<>();
    protected final List<Argument> optionalArguments = new ArrayList<>();
    protected final List<Impl> children = new ArrayList<>();
    protected final Map<Class<? extends Executor>, Executor> executors = new HashMap<>();

    protected final String name;
    protected final List<String> aliases;
    protected final String description;

    protected UsageRequirement<Source> requirement = UsageRequirement.none();
    protected Set<Permission> permissions = new HashSet<>();

    protected Impl parent = null;

    public AbstractCommandBuilder(String name, List<String> aliases, String description) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
    }

    public AbstractCommandBuilder(String name, String description, String... aliases) {
        this(name, List.of(aliases), description);
    }

    public AbstractCommandBuilder(String name, String... aliases) {
        this(name, List.of(aliases), "");
    }

    public Impl withArguments(Argument @NotNull ... args) {
        requiredArguments.addAll(Arrays.asList(args));
        return self();
    }

    public Impl withOptionalArguments(Argument @NotNull ... args) {
        optionalArguments.addAll(Arrays.asList(args));
        return self();
    }

    public Impl withRequirement(@NotNull UsageRequirement<Source> requirement) {
        this.requirement = this.requirement.and(requirement);
        return self();
    }

    public Impl withPermissions(Permission... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        return self();
    }

    public Impl withSubCommands(Impl @NotNull ... children) {
        for (Impl child : children) {
            this.children.add(child.asChildOf(self()));
        }

        return self();
    }

    public <E extends Executor> Impl executes(Class<E> type, E executor) {
        executors.put(type, executor);
        return self();
    }

    public Definition register(@NotNull CommandsRegistry<?, Definition> registry) {
        Definition definition = build();
        registry.register(definition);

        return definition;
    }

    public Definition register(@NotNull Platform<? extends CommandsRegistry<?, Definition>> platform) {
        return register(platform.getCommandsRegistry());
    }

    @SuppressWarnings("unchecked")
    public Impl self() {
        return (Impl) this;
    }

    public abstract Definition build(@Nullable Definition parent);

    public Definition build() {
        return build(null);
    }

    protected Impl asChildOf(Impl parent) {
        this.parent = parent;
        return self();
    }

}
