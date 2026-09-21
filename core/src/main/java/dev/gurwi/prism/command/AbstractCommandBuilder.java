package dev.gurwi.prism.command;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.requirement.ExecutionRequirement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractCommandBuilder<
        Impl extends AbstractCommandBuilder<Impl, Definition, Source, Executor, Argument>,
        Definition extends AbstractCommandDefinition<Source, Executor, Argument>,
        Source,
        Executor extends CommandExecutor<?>,
        Argument extends AbstractArgument<?, ?, ?>
> {

    protected final List<Argument> requiredArguments = new ArrayList<>();
    protected final List<Argument> optionalArguments = new ArrayList<>();
    protected final Map<Class<? extends Executor>, Executor> executors = new HashMap<>();

    protected final String name;
    protected final List<String> aliases;
    protected final String description;

    protected ExecutionRequirement<Source> requirement = ExecutionRequirement.none();

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

    public Impl withRequirement(ExecutionRequirement<Source> requirement) {
        this.requirement = requirement;
        return self();
    }

    public <E extends Executor> Impl executes(Class<E> type, E executor) {
        executors.put(type, executor);
        return self();
    }

    @SuppressWarnings("unchecked")
    public Impl self() {
        return (Impl) this;
    }

    public abstract Definition build();

}
