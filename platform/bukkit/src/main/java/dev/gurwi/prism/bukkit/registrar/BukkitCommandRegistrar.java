package dev.gurwi.prism.bukkit.registrar;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface BukkitCommandRegistrar {

    void register(BukkitCommandDefinition definition);

    void unregister(BukkitCommandDefinition definition);

    @Unmodifiable
    Set<BukkitCommandDefinition> getRegisteredCommands();

    Type getType();

    enum Type {
        LEGACY,
        BRIGADIER
    }

}
