package club.athlas.athlasdevtemplate.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public interface AthlasDevTemplateAPI {

    static @Nullable AthlasDevTemplateAPI getAPI() {
        if (Bukkit.getPluginManager().getPlugin("PluginName") == null) return null;
        RegisteredServiceProvider<AthlasDevTemplateAPI> serviceProvider = Bukkit.getServicesManager().getRegistration(AthlasDevTemplateAPI.class);

        if (serviceProvider == null) return null;

        return serviceProvider.getProvider();
    }

}
