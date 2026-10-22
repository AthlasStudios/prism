package club.athlas.prism.manager;

import club.athlas.prism.PrismPlugin;
import club.athlas.prism.api.PluginRegistriesManager;
import club.athlas.prism.config.PrismConfiguration;
import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.BukkitPlatform;
import dev.gurwi.prism.paper.PaperPlatform;
import dev.gurwi.prism.paper.PaperPlatformConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BasePluginRegistriesManager implements PluginRegistriesManager {

    private final Map<Plugin, BukkitCommandsRegistry<?>> registries = new HashMap<>();

    private final PrismPlugin plugin;

    public BasePluginRegistriesManager(PrismPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BukkitCommandsRegistry<?> createRegistry(@NotNull Plugin plugin) {
        this.plugin.getLogger().info("Creating registry for " + plugin.getName());

        BukkitPlatform<?, ?> platform = createPlatform(plugin);
        registries.put(plugin, platform.getCommandsRegistry());

        return platform.getCommandsRegistry();
    }

    @Override
    public void unloadRegistry(@NotNull Plugin plugin) {
        BukkitCommandsRegistry<?> removedRegistry = registries.remove(plugin);

        if (removedRegistry == null) return;

        this.plugin.getLogger().info("Unloading registry for " + plugin.getName());

        new ArrayList<>(removedRegistry.getRegisteredCommands()).forEach(removedRegistry::unregister);
    }

    @Override
    public Optional<BukkitCommandsRegistry<?>> getRegistry(@NotNull Plugin plugin) {
        return Optional.ofNullable(registries.get(plugin));
    }

    public BukkitPlatform<?, ?> createPlatform(Plugin plugin) {
        PaperPlatformConfiguration.Builder configure = PaperPlatform.configure(plugin);

        if (PrismConfiguration.disableBrigadierRegistrar) configure.disableBrigadier();
        if (PrismConfiguration.disableLegacyRegistrar) configure.disableLegacyRegistrar();
        if (PrismConfiguration.disableAsyncSuggestions) configure.disableAsyncSuggestions();

        configure.registerPermissions(PrismConfiguration.registerPermissions);

        return configure.init();
    }

}