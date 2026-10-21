package club.athlas.prism.listener;

import club.athlas.prism.PrismPlugin;
import club.athlas.prism.api.PrismCommandProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PluginsListener implements Listener {

    private final PrismPlugin plugin;

    public PluginsListener(PrismPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginEnable(@NotNull PluginEnableEvent event) {
        Plugin target = event.getPlugin();

        if (target instanceof PrismCommandProvider) {
            plugin.getProvidersManager().register(target, (PrismCommandProvider) target);

            if (!plugin.getDescription().getDepend().contains(plugin.getName())) {
                plugin.getLogger().warning("Plugin " + target.getName() + " should depend on prism to work correctly.");
            }
        }
    }

    @EventHandler
    public void onPluginDisable(@NotNull PluginDisableEvent event) {
        Plugin target = event.getPlugin();

        plugin.getProvidersManager().unregister(target);
        plugin.getRegistriesManager().unloadRegistry(target);
    }

}
