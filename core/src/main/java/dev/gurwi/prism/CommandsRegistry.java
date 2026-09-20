package dev.gurwi.prism;

import dev.gurwi.prism.command.AbstractCommandBuilder;
import dev.gurwi.prism.command.definition.CommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;

public interface CommandsRegistry<Impl extends CommandsRegistry<Impl, PlatformType, Definition, DefinitionBuilder>, PlatformType extends Platform<Impl>, Definition extends CommandDefinition<Definition, PlatformType, Impl, ?, ?>, DefinitionBuilder extends AbstractCommandBuilder<?, PlatformType, Impl, Definition, ?, ?>> extends ChainableBuilder<Impl> {

    Impl register(@NotNull Collection<Definition> commands);

    default Impl register(Definition... commands) {
        return register(List.of(commands));
    }

    default Impl registerBuilders(@NotNull Collection<DefinitionBuilder> builders) {
        builders.forEach(builder -> register(builder.build()));
        return instance();
    }

    default Impl register(DefinitionBuilder... builders) {
        return registerBuilders(List.of(builders));
    }

    Impl unregister(Collection<Definition> commands);

    default Impl unregister(Definition... commands) {
        return unregister(List.of(commands));
    }

    @Unmodifiable
    List<Definition> getRegisteredCommands();

    PlatformType getPlatform();

}
