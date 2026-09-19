package dev.gurwi.prism.command.definition;

import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.argument.AbstractArgument;

import java.util.List;
import java.util.Map;

public class SubCommandDefinition<P extends Platform<? extends CommandsRegistry<?, P, E, A>>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>> extends CommandDefinition<P, E, A> {

    private final CommandDefinition<P, E, A> parent;

    public SubCommandDefinition(CommandDefinition<P, E, A> parent, String name, String[] aliases, List<A> requiredArguments, List<A> optionalArguments, Map<Class<? extends E>, E> executors) {
        super(name, aliases, requiredArguments, optionalArguments, executors);
        this.parent = parent;
    }

}
