package dev.gurwi.prism.bukkit;

import dev.gurwi.prism.platform.CommandPlatform;
import org.bukkit.plugin.Plugin;

public interface BukkitCommandPlatform<Registry extends BukkitCommandsRegistry<? extends BukkitCommandPlatform<Registry, Configuration>>, Configuration extends BukkitCommandPlatformConfiguration<? extends BukkitCommandPlatform<Registry, Configuration>>> extends CommandPlatform<Registry> {

    Configuration getConfiguration();

    Plugin getPlugin();

}
