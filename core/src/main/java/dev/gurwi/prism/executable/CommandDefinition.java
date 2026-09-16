package dev.gurwi.prism.executable;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>> {

    private final Map<Class<? extends E>, E> executors = new HashMap<>();
    private final Map<String, SubCommandDefinition<P, E>> children = new HashMap<>();

    private final String name;
    private final String[] aliases;

    public CommandDefinition(String name, String[] aliases, Map<Class<? extends E>, E> executors) {
        this.name = name;
        this.aliases = aliases;

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
    public @NotNull Map<Class<? extends E>, E> getExecutors() {
        return Map.copyOf(executors);
    }

    @SuppressWarnings("unchecked")
    public <T extends E> T getExecutor(Class<T> type) {
        return (T) executors.get(type);
    }

    @Unmodifiable
    public List<SubCommandDefinition<P, E>> getChildren() {
        return List.copyOf(children.values());
    }

    public void register(@NotNull CommandsRegistry<?, P, E> registry) {
        registry.register(this);
    }

    public void register(@NotNull P platform) {
        register(platform.getRegistry());
    }

    public void unregister(@NotNull CommandsRegistry<?, P, E> registry) {
        registry.unregister(this);
    }

    public void unregister(@NotNull P platform) {
        unregister(platform.getRegistry());
    }

}
