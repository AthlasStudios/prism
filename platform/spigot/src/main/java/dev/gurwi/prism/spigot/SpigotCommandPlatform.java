package dev.gurwi.prism.spigot;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.bukkit.BukkitCommandPlatform;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SpigotCommandPlatform implements BukkitCommandPlatform<SpigotCommandsRegistry, SpigotCommandPlatformConfiguration> {

    private final Plugin plugin;
    private final SpigotCommandPlatformConfiguration configuration;
    private final SpigotCommandsRegistry commandsRegistry;

    protected SpigotCommandPlatform(Plugin plugin, SpigotCommandPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.commandsRegistry = new SpigotCommandsRegistry(this);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull SpigotCommandPlatformConfiguration.Builder configure(JavaPlugin plugin) {
        return new SpigotCommandPlatformConfiguration.Builder(plugin);
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
    public SpigotCommandPlatformConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

}
