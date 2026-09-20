package dev.gurwi.prism.spigot.registrar;

import dev.gurwi.prism.spigot.command.SpigotCommand;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface CommandsRegistrar {

    void register(SpigotCommand definition);

    void unregister(SpigotCommand definition);

    @Unmodifiable
    List<SpigotCommand> getRegisteredCommands();

}
