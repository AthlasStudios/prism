package club.athlas.prism.api;

import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface PluginRegistriesManager {

    BukkitCommandsRegistry<?> createRegistry(@NotNull Plugin plugin);

    void unloadRegistry(@NotNull Plugin plugin);

    Optional<BukkitCommandsRegistry<?>> getRegistry(@NotNull Plugin plugin);

}
