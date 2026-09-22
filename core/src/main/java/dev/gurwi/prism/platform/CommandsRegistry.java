package dev.gurwi.prism.platform;

import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface CommandsRegistry<PlatformType extends Platform<? extends CommandsRegistry<PlatformType, Definition>>, Definition extends AbstractCommandDefinition<?, ?, ?, ?>> {

    void register(Definition definition);

    void unregister(Definition definition);

    @Unmodifiable
    Set<Definition> getRegisteredCommands();

    PlatformType getPlatform();

}
