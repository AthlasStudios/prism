package dev.gurwi.prism.command;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.requirement.ExecutionRequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public abstract class AbstractCommandDefinition<
        Source,
        Executor extends CommandExecutor<?>,
        Argument extends AbstractArgument<?, ?>
> {

    private final Map<Class<? extends Executor>, Executor> executors = new HashMap<>();

    private final String name;
    private final List<String> aliases;
    private final String description;
    private final ExecutionRequirement<Source> requirement;

    protected AbstractCommandDefinition(String name, List<String> aliases, String description, ExecutionRequirement<Source> requirement, Map<Class<? extends Executor>, Executor> executors) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.requirement = requirement;

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

    public <E extends Executor> Optional<E> getExecutor(@NotNull Class<E> type) {
        return Optional.ofNullable(type.cast(executors.get(type)));
    }

    public @Unmodifiable Set<Executor> getExecutors() {
        return Set.copyOf(executors.values());
    }

}
