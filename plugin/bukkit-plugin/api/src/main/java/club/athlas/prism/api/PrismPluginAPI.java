package club.athlas.prism.api;

import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public interface PrismPluginAPI {

    void registerProvider(Plugin plugin, PrismCommandProvider provider, @Nullable BukkitCommandsRegistry<?> registry);

    BukkitCommandsRegistry<?> createRegistry(Plugin plugin);

    BukkitCommandsRegistry<?> getDefaultRegistry();

    PluginRegistriesManager getRegistriesManager();

    static @Nullable PrismPluginAPI getAPI() {
        if (Bukkit.getPluginManager().getPlugin("prism") == null) return null;
        RegisteredServiceProvider<PrismPluginAPI> serviceProvider = Bukkit.getServicesManager().getRegistration(PrismPluginAPI.class);

        if (serviceProvider == null) return null;

        return serviceProvider.getProvider();
    }

}
