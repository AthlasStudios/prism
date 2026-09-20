package dev.gurwi.prism.command;

import dev.gurwi.prism.*;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.definition.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractCommandBuilder<Impl extends AbstractCommandBuilder<Impl, P, R, D, E, A>, P extends Platform<R>, R extends CommandsRegistry<R, P, D, Impl>, D extends CommandDefinition<D, P, R, E, A>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>> implements ChainableBuilder<Impl> {

    protected final Map<Class<? extends E>, E> executors = new HashMap<>();
    protected final Set<ExecutionRequirement<?>> requirements = new HashSet<>();
    protected final List<A> requiredArguments = new ArrayList<>();
    protected final List<A> optionalArguments = new ArrayList<>();
    protected final Map<String, Impl> subCommands = new HashMap<>();

    protected final String name;
    protected final String[] aliases;

    protected AbstractCommandBuilder(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public Impl withArguments(A... arguments) {
        requiredArguments.addAll(Arrays.asList(arguments));
        return instance();
    }

    public Impl withSubCommand(Impl @NotNull ... subCommands) {
        for (Impl sub : subCommands) {
            this.subCommands.put(sub.name, sub);
        }

        return instance();
    }

    public <Executor extends E> Impl executes(Class<Executor> type, Executor executor) {
        executors.put(type, executor);
        return instance();
    }

    public abstract D build();

    public D register(R registry) {
        D definition = build();
        definition.register(registry);

        return definition;
    }

    public D register(@NotNull P platform) {
        return register(platform.getRegistry());
    }

}
