package dev.gurwi.prism.platform;

import dev.gurwi.prism.command.AbstractCommandDefinition;

public interface CommandsRegistry<PlatformType extends Platform<? extends CommandsRegistry<PlatformType, Definition>>, Definition extends AbstractCommandDefinition<?, ?, ?>> {

    void register(Definition definition);

    void unregister(Definition definition);

    PlatformType getPlatform();

}
