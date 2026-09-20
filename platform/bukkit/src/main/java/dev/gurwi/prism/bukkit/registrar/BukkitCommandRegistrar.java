package dev.gurwi.prism.bukkit.registrar;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;

public interface BukkitCommandRegistrar {

    void register(BukkitCommandDefinition definition);

    void unregister(BukkitCommandDefinition definition);

}
