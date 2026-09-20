package dev.gurwi.prism.platform;

import dev.gurwi.prism.command.AbstractCommandDefinition;

public interface CommandsRegistry<Definition extends AbstractCommandDefinition<?, ?, ?>> {

    void register(Definition definition);

    void unregister(Definition definition);

}
