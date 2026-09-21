package dev.gurwi.prism.bukkit;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.platform.CommandsRegistry;

public interface BukkitCommandsRegistry<Platform extends BukkitPlatform<? extends BukkitCommandsRegistry<Platform>>> extends CommandsRegistry<Platform, BukkitCommandDefinition> {
}
