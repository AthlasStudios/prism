package dev.gurwi.prism.command;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.requirement.ExecutionRequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public abstract class AbstractCommandDefinition<
        Source,
        Executor extends CommandExecutor<?>,
        Argument extends AbstractArgument<?, ?, ?>
> {

    private final Map<Class<? extends Executor>, Executor> executors = new HashMap<>();
    private final List<Argument> requiredArguments = new ArrayList<>();
    private final List<Argument> optionalArguments = new ArrayList<>();

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final ExecutionRequirement<Source> requirement;

    protected AbstractCommandDefinition(String name, List<String> aliases, String description, ExecutionRequirement<Source> requirement, List<Argument> requiredArguments, List<Argument> optionalArguments, Map<Class<? extends Executor>, Executor> executors) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.requirement = requirement;

        this.requiredArguments.addAll(requiredArguments);
        this.optionalArguments.addAll(optionalArguments);
        this.executors.putAll(executors);
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

    public @Unmodifiable List<Argument> getRequiredArguments() {
        return List.copyOf(requiredArguments);
    }

    public @Unmodifiable List<Argument> getOptionalArguments() {
        return List.copyOf(optionalArguments);
    }

    public <E extends Executor> Optional<E> getExecutor(@NotNull Class<E> type) {
        return Optional.ofNullable(type.cast(executors.get(type)));
    }

    public @Unmodifiable Set<Executor> getExecutors() {
        return Set.copyOf(executors.values());
    }

}
