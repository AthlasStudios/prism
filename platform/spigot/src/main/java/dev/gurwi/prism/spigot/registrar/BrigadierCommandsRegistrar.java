package dev.gurwi.prism.spigot.registrar;

import dev.gurwi.prism.bukkit.argument.Argument;
import dev.gurwi.prism.command.definition.CommandDefinition;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class BrigadierCommandsRegistrar implements CommandsRegistrar {

    @Override
    public void register(CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>, Argument<?, ?>> definition) {

    }

    @Override
    public void unregister(CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>, Argument<?, ?>> definition) {

    }

    @Override
    public @Unmodifiable List<CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>, Argument<?, ?>>> getRegisteredCommands() {
        return List.of();
    }

}
