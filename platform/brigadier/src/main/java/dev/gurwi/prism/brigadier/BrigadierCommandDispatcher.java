package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import dev.gurwi.prism.CommandExecutor;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.Platform;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.definition.CommandDefinition;

public class BrigadierCommandDispatcher<P extends Platform<? extends CommandsRegistry<?, P, E, A>>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>, S> extends CommandDispatcher<S> {

    private final BrigadierCommandMapper<S> commandMapper = new BrigadierCommandMapper<>();

    public void register(CommandDefinition<P, E, A> commandDefinition) {

    }

    public void unregister() {

    }

}
