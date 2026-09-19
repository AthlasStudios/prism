package dev.gurwi.prism.command;

import dev.gurwi.prism.*;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.definition.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractCommand<Impl extends AbstractCommand<Impl, P, E, A>, P extends Platform<? extends CommandsRegistry<?, P, E, A>>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>> implements ChainableBuilder<Impl> {

    protected final Map<Class<? extends E>, E> executors = new HashMap<>();
    protected final Set<ExecutionRequirement<?>> requirements = new HashSet<>();
    protected final List<A> requiredArguments = new ArrayList<>();
    protected final List<A> optionalArguments = new ArrayList<>();
    protected final Map<String, Impl> subCommands = new HashMap<>();

    protected final String name;
    protected final String[] aliases;

    protected AbstractCommand(String name, String... aliases) {
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

    public CommandDefinition<P, E, A> build() {
        return new CommandDefinition<>(name, aliases, requiredArguments, optionalArguments, executors);
    }

    public CommandDefinition<P, E, A> register(CommandsRegistry<?, P, E, A> registry) {
        CommandDefinition<P, E, A> definition = build();
        definition.register(registry);

        return definition;
    }

    public <R extends CommandsRegistry<?, P, E, A>> CommandDefinition<P, E, A> register(@NotNull Platform<R> platform) {
        return register(platform.getRegistry());
    }

}
