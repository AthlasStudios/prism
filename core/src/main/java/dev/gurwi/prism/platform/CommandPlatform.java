package dev.gurwi.prism.platform;

public interface CommandPlatform<Registry extends CommandsRegistry<?, ?>> {

    String getIdentifier();

    Registry getCommandsRegistry();

}
