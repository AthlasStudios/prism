package dev.gurwi.prism.spigot;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.bukkit.BukkitPlatform;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatform implements BukkitPlatform<SpigotCommandsRegistry, SpigotPlatformConfiguration> {

    private final Plugin plugin;
    private final SpigotPlatformConfiguration configuration;
    private final SpigotCommandsRegistry commandsRegistry;

    protected SpigotPlatform(Plugin plugin, SpigotPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.commandsRegistry = new SpigotCommandsRegistry(this);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull SpigotPlatformConfiguration.Builder configure(JavaPlugin plugin) {
        return new SpigotPlatformConfiguration.Builder(plugin);
    }

    @Override
    public String getIdentifier() {
        return Prism.SPIGOT_PLATFORM_IDENTIFIER;
    }

    @Override
    public SpigotCommandsRegistry getCommandsRegistry() {
        return commandsRegistry;
    }

    @Override
    public SpigotPlatformConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

}
