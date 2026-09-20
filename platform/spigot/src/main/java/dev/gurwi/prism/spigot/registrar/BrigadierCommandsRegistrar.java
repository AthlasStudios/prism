package dev.gurwi.prism.spigot.registrar;

import dev.gurwi.prism.spigot.command.SpigotCommand;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class BrigadierCommandsRegistrar implements CommandsRegistrar {

    @Override
    public void register(SpigotCommand definition) {

    }

    @Override
    public void unregister(SpigotCommand definition) {

    }

    @Override
    public @Unmodifiable List<SpigotCommand> getRegisteredCommands() {
        return List.of();
    }

}
