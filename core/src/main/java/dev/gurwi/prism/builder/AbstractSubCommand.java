package dev.gurwi.prism.builder;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;

public abstract class AbstractSubCommand<Impl extends AbstractSubCommand<Impl, P, E>, P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>> extends AbstractCommand<Impl, P, E> {

    protected AbstractCommand<?, P, E> parent;

    protected AbstractSubCommand(String name, String... aliases) {
        super(name, aliases);
    }

}
