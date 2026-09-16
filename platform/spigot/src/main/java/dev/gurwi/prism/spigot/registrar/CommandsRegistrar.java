package dev.gurwi.prism.spigot.registrar;

import dev.gurwi.prism.executable.CommandDefinition;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface CommandsRegistrar {

    void register(CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> definition);

    void unregister(CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> definition);

    @Unmodifiable
    List<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>>> getRegisteredCommands();

}
