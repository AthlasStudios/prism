package dev.gurwi.prism.command.definition;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.argument.AbstractArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public abstract class CommandDefinition<Impl extends CommandDefinition<Impl, PlatformType, Registry, Executor, Argument>, PlatformType extends Platform<Registry>, Registry extends CommandsRegistry<Registry, PlatformType, Impl, ?>, Executor extends CommandExecutor<?>, Argument extends AbstractArgument<?, ?, ?>> {

    private final List<Argument> requiredArguments = new ArrayList<>();
    private final List<Argument> optionalArguments = new ArrayList<>();
    private final Map<Class<? extends Executor>, Executor> executors = new HashMap<>();
    private final Map<String, Impl> children = new HashMap<>();

    private final String name;
    private final String[] aliases;

    public CommandDefinition(String name, String[] aliases, List<Argument> requiredArguments, List<Argument> optionalArguments, Map<Class<? extends Executor>, Executor> executors) {
        this.name = name;
        this.aliases = aliases;

        this.requiredArguments.addAll(requiredArguments);
        this.optionalArguments.addAll(optionalArguments);
        this.executors.putAll(executors);
    }

    public String getName() {
        return name;
    }

    @Unmodifiable
    public Set<String> getAliases() {
        return Set.of(aliases);
    }

    @Unmodifiable
    public List<Argument> getRequiredArguments() {
        return List.copyOf(requiredArguments);
    }

    @Unmodifiable
    public List<Argument> getOptionalArguments() {
        return List.copyOf(optionalArguments);
    }

    @Unmodifiable
    public @NotNull Map<Class<? extends Executor>, Executor> getExecutors() {
        return Map.copyOf(executors);
    }

    @SuppressWarnings("unchecked")
    public <T extends Executor> T getExecutor(Class<T> type) {
        return (T) executors.get(type);
    }

    @Unmodifiable
    public List<Impl> getChildren() {
        return List.copyOf(children.values());
    }

    public void register(@NotNull Registry registry) {
        registry.register(instance());
    }

    public void register(@NotNull PlatformType platform) {
        register(platform.getRegistry());
    }

    public void unregister(@NotNull Registry registry) {
        registry.unregister(instance());
    }

    public void unregister(@NotNull PlatformType platform) {
        unregister(platform.getRegistry());
    }

    public abstract Impl instance();

}
