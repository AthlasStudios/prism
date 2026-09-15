package dev.gurwi.prism.spigot;

import dev.gurwi.prism.executable.CommandDefinition;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class SpigotCommandsRegistry implements CommandsRegistry<SpigotCommandsRegistry, SpigotPlatform, SpigotCommandExecutor<?>> {

    private final SpigotPlatform platform;

    public SpigotCommandsRegistry(SpigotPlatform platform) {
        this.platform = platform;
    }

    @Override
    public SpigotCommandsRegistry register(@NotNull Collection<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>>> commandDefinitions) {
        return null;
    }

    @Override
    public SpigotCommandsRegistry unregister(Collection<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>>> commandDefinitions) {
        return null;
    }

    @Override
    public List<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>>> getRegisteredCommands() {
        return List.of();
    }

    @Override
    public SpigotPlatform getPlatform() {
        return platform;
    }

    @Override
    public SpigotCommandsRegistry instance() {
        return this;
    }

}
