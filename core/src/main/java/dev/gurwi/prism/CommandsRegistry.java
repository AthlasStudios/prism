package dev.gurwi.prism;

import dev.gurwi.prism.builder.AbstractCommand;
import dev.gurwi.prism.executable.CommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;

public interface CommandsRegistry<Impl extends CommandsRegistry<Impl, P, E>, P extends Platform<? extends CommandsRegistry<?, P, E>>, E extends CommandExecutor<?>> extends ChainableBuilder<Impl> {

    Impl register(@NotNull Collection<CommandDefinition<P, E>> commands);

    default Impl register(CommandDefinition<P, E>... commands) {
        return register(List.of(commands));
    }

    default Impl registerBuilders(@NotNull Collection<AbstractCommand<?, P, E>> builders) {
        builders.forEach(builder -> register(builder.build()));
        return instance();
    }

    default Impl register(AbstractCommand<?, P, E>... builders) {
        return registerBuilders(List.of(builders));
    }

    Impl unregister(Collection<CommandDefinition<P, E>> commands);

    default Impl unregister(CommandDefinition<P, E>... commands) {
        return unregister(List.of(commands));
    }

    @Unmodifiable
    List<CommandDefinition<P, E>> getRegisteredCommands();

    P getPlatform();

}
