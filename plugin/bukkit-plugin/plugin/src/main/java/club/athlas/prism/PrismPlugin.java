package club.athlas.prism;

import club.athlas.prism.api.PluginRegistriesManager;
import club.athlas.prism.api.PrismCommandProvider;
import club.athlas.prism.api.PrismPluginAPI;
import club.athlas.prism.command.PrismInfoCommand;
import club.athlas.prism.config.PrismConfiguration;
import club.athlas.prism.listener.PluginsListener;
import club.athlas.prism.manager.BasePluginRegistriesManager;
import club.athlas.prism.manager.ProvidersManager;
import dev.gurwi.confetti.Confetti;
import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.BukkitPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class PrismPlugin extends JavaPlugin implements PrismPluginAPI {

    private final ProvidersManager providersManager = new ProvidersManager();
    private final BasePluginRegistriesManager registriesManager = new BasePluginRegistriesManager(this);

    private BukkitPlatform<?, ?> platform;

    @Override
    public void onEnable() {
        new Confetti().load(PrismConfiguration.class, getDataFolder());
        platform = registriesManager.createPlatform(this);

        Bukkit.getServicesManager().register(
                PrismPluginAPI.class,
                this,
                this,
                ServicePriority.Normal
        );

        if (!PrismConfiguration.disableInfoCommand) new PrismInfoCommand().register(platform);

        getServer().getPluginManager().registerEvents(new PluginsListener(this), this);
    }

    @Override
    public void registerProvider(Plugin plugin, PrismCommandProvider provider, @Nullable BukkitCommandsRegistry<?> registry) {
        BukkitCommandsRegistry<?> target = registry;

        if (target == null) target = provider.getRegistry();
        if (target == null) target = registriesManager.getRegistry(plugin).orElseGet(() -> registriesManager.createRegistry(plugin));

        providersManager.register(plugin, provider);

        BukkitCommandsRegistry<?> destination = target;

        provider.getCommands().forEach(command -> destination.register(command.build()));
    }

    @Override
    public BukkitCommandsRegistry<?> createRegistry(Plugin plugin) {
        return registriesManager.createRegistry(plugin);
    }

    @Override
    public BukkitCommandsRegistry<?> getDefaultRegistry() {
        return platform.getCommandsRegistry();
    }

    @Override
    public PluginRegistriesManager getRegistriesManager() {
        return registriesManager;
    }

    public ProvidersManager getProvidersManager() {
        return providersManager;
    }

}