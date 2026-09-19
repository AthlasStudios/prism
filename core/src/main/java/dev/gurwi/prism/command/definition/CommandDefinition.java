package dev.gurwi.prism.command.definition;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.argument.AbstractArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class CommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E, A>>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>> {

    private final List<A> requiredArguments = new ArrayList<>();
    private final List<A> optionalArguments = new ArrayList<>();
    private final Map<Class<? extends E>, E> executors = new HashMap<>();
    private final Map<String, SubCommandDefinition<P, E, A>> children = new HashMap<>();

    private final String name;
    private final String[] aliases;

    public CommandDefinition(String name, String[] aliases, List<A> requiredArguments, List<A> optionalArguments, Map<Class<? extends E>, E> executors) {
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
    public List<A> getRequiredArguments() {
        return List.copyOf(requiredArguments);
    }

    @Unmodifiable
    public List<A> getOptionalArguments() {
        return List.copyOf(optionalArguments);
    }

    @Unmodifiable
    public @NotNull Map<Class<? extends E>, E> getExecutors() {
        return Map.copyOf(executors);
    }

    @SuppressWarnings("unchecked")
    public <T extends E> T getExecutor(Class<T> type) {
        return (T) executors.get(type);
    }

    @Unmodifiable
    public List<SubCommandDefinition<P, E, A>> getChildren() {
        return List.copyOf(children.values());
    }

    public void register(@NotNull CommandsRegistry<?, P, E, A> registry) {
        registry.register(this);
    }

    public void register(@NotNull P platform) {
        register(platform.getRegistry());
    }

    public void unregister(@NotNull CommandsRegistry<?, P, E, A> registry) {
        registry.unregister(this);
    }

    public void unregister(@NotNull P platform) {
        unregister(platform.getRegistry());
    }

}
