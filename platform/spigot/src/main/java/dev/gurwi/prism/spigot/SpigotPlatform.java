package dev.gurwi.prism.spigot;

import dev.gurwi.prism.Platform;
import dev.gurwi.prism.Prism;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SpigotPlatform implements Platform<SpigotCommandsRegistry> {

    private final SpigotCommandsRegistry commandsRegistry;
    private final JavaPlugin plugin;
    private final SpigotPlatformConfiguration configuration;

    SpigotPlatform(JavaPlugin plugin, SpigotPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.commandsRegistry = new SpigotCommandsRegistry(this);
    }

    @Contract("_ -> new")
    public static @NotNull SpigotPlatformConfiguration.Builder configure(JavaPlugin plugin) {
        return new SpigotPlatformConfiguration.Builder(plugin);
    }

    @Override
    public @NotNull String getIdentifier() {
        return Prism.SPIGOT_PLATFORM_IDENTIFIER;
    }

    @Override
    public @NotNull SpigotCommandsRegistry getRegistry() {
        return commandsRegistry;
    }

    public JavaPlugin getOwningPlugin() {
        return plugin;
    }

}
