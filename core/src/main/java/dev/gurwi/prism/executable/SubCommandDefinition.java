package dev.gurwi.prism.executable;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.builder.AbstractCommand;

import java.util.Map;

public class SubCommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>> extends CommandDefinition<P, E> {

    private final CommandDefinition<P, E> parent;

    public SubCommandDefinition(CommandDefinition<P, E> parent, String name, String[] aliases, Map<Class<? extends E>, E> executors) {
        super(name, aliases, executors);
        this.parent = parent;
    }

}
