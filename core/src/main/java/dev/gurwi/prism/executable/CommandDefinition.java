package dev.gurwi.prism.executable;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.builder.AbstractCommand;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>> {

    private final Map<Class<CommandExecutor<?>>, CommandExecutor<?>> executors = new HashMap<>();
    private final Map<String, SubCommandDefinition<P, E>> subCommands = new HashMap<>();

    protected final AbstractCommand<?, P, E> command;
    private final String name;

    public CommandDefinition(AbstractCommand<?, P, E> command) {
        this.command = command;
        this.name = "TODO";
    }

    public String getName() {
        return name;
    }

    public void register(@NotNull CommandsRegistry<?, P, E> registry) {
        registry.register(this);
    }

    public void register(@NotNull P platform) {
        register(platform.getRegistry());
    }

}
