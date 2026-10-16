package dev.gurwi.prism.bukkit;

import dev.gurwi.prism.platform.Platform;
import org.bukkit.plugin.java.JavaPlugin;

public interface BukkitPlatform<Registry extends BukkitCommandsRegistry<? extends BukkitPlatform<Registry>>> extends Platform<Registry> {

    JavaPlugin getPlugin();

}
