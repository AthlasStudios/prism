package dev.gurwi.prism.builder;

import dev.gurwi.prism.*;
import dev.gurwi.prism.executable.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractCommand<Impl extends AbstractCommand<Impl, P, E>, P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>> implements ChainableBuilder<Impl> {

    protected final Map<Class<? extends E>, E> executors = new HashMap<>();
    protected final Set<ExecutionRequirement<?>> requirements = new HashSet<>();
    protected final Map<String, Impl> subCommands = new HashMap<>();

    protected final String name;
    protected final String[] aliases;

    protected AbstractCommand(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    @SafeVarargs
    public final Impl withSubCommand(Impl @NotNull ... subCommands) {
        for (Impl sub : subCommands) {
            this.subCommands.put(sub.name, sub);
        }

        return instance();
    }

    public void register(CommandsRegistry<?, P, E> registry) {
        new CommandDefinition<>(this).register(registry);
    }

    public <R extends CommandsRegistry<?, P, E>> void register(@NotNull Platform<R> platform) {
        new CommandDefinition<>(this).register(platform.getRegistry());
    }

}
