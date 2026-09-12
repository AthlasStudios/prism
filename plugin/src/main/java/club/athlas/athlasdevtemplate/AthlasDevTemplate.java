package club.athlas.athlasdevtemplate;

import club.athlas.athlasdevtemplate.api.AthlasDevTemplateAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class AthlasDevTemplate extends JavaPlugin implements AthlasDevTemplateAPI {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServicesManager().register(AthlasDevTemplateAPI.class, this, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getServicesManager().unregister(this);
    }

}
