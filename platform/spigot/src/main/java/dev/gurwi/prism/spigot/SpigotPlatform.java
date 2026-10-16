package dev.gurwi.prism.spigot;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.bukkit.BukkitPlatform;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatform implements BukkitPlatform<SpigotCommandsRegistry> {

    private final JavaPlugin plugin;
    private final SpigotPlatformConfiguration configuration;
    private final SpigotCommandsRegistry commandsRegistry;

    protected SpigotPlatform(JavaPlugin plugin, SpigotPlatformConfiguration configuration) {
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
    public JavaPlugin getPlugin() {
        return plugin;
    }

    public SpigotPlatformConfiguration getConfiguration() {
        return configuration;
    }

}
