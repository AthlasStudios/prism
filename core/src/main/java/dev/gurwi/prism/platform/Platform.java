package dev.gurwi.prism.platform;

public interface Platform<Registry extends CommandsRegistry<?>> {

    String getIdentifier();

    Registry getCommandsRegistry();

}
