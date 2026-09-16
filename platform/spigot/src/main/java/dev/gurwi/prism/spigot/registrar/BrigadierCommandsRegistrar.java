package dev.gurwi.prism.spigot.registrar;

import dev.gurwi.prism.executable.CommandDefinition;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class BrigadierCommandsRegistrar implements CommandsRegistrar {

    @Override
    public void register(CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> definition) {

    }

    @Override
    public void unregister(CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> definition) {

    }

    @Override
    public @Unmodifiable List<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>>> getRegisteredCommands() {
        return List.of();
    }

}
