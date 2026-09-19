package dev.gurwi.prism.command;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.argument.AbstractArgument;

public abstract class AbstractSubCommand<Impl extends AbstractSubCommand<Impl, P, E, A>, P extends Platform<? extends CommandsRegistry<?, P, E, A>>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>> extends AbstractCommand<Impl, P, E, A> {

    protected AbstractCommand<?, P, E, A> parent;

    protected AbstractSubCommand(String name, String... aliases) {
        super(name, aliases);
    }

}
