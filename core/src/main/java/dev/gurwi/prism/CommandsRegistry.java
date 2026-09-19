package dev.gurwi.prism;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.AbstractCommand;
import dev.gurwi.prism.command.definition.CommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;

public interface CommandsRegistry<Impl extends CommandsRegistry<Impl, P, E, A>, P extends Platform<? extends CommandsRegistry<?, P, E, A>>, E extends CommandExecutor<?>, A extends AbstractArgument<?, ?, ?>> extends ChainableBuilder<Impl> {

    Impl register(@NotNull Collection<CommandDefinition<P, E, A>> commands);

    default Impl register(CommandDefinition<P, E, A>... commands) {
        return register(List.of(commands));
    }

    default Impl registerBuilders(@NotNull Collection<AbstractCommand<?, P, E, A>> builders) {
        builders.forEach(builder -> register(builder.build()));
        return instance();
    }

    default Impl register(AbstractCommand<?, P, E, A>... builders) {
        return registerBuilders(List.of(builders));
    }

    Impl unregister(Collection<CommandDefinition<P, E, A>> commands);

    default Impl unregister(CommandDefinition<P, E, A>... commands) {
        return unregister(List.of(commands));
    }

    @Unmodifiable
    List<CommandDefinition<P, E, A>> getRegisteredCommands();

    P getPlatform();

}
