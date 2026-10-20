package dev.gurwi.prism.bukkit;

import dev.gurwi.prism.platform.Platform;
import org.bukkit.plugin.Plugin;

public interface BukkitPlatform<Registry extends BukkitCommandsRegistry<? extends BukkitPlatform<Registry, Configuration>>, Configuration extends BukkitPlatformConfiguration<? extends BukkitPlatform<Registry, Configuration>>> extends Platform<Registry> {

    Configuration getConfiguration();

    Plugin getPlugin();

}
