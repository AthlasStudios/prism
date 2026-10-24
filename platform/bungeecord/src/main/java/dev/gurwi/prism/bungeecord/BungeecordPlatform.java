package dev.gurwi.prism.bungeecord;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.platform.Platform;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BungeecordPlatform implements Platform<BungeecordCommandsRegistry> {

    private final Plugin plugin;
    private final BungeecordPlatformConfiguration configuration;
    private final BungeecordCommandsRegistry registry;

    protected BungeecordPlatform(@NotNull Plugin plugin, @NotNull BungeecordPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;

        this.registry = new BungeecordCommandsRegistry(this);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull BungeecordPlatformConfiguration.Builder configure(@NotNull Plugin plugin) {
        return new BungeecordPlatformConfiguration.Builder(plugin);
    }

    @Override
    public String getIdentifier() {
        return Prism.BUNGEECORD_PLATFORM_IDENTIFIER;
    }

    @Override
    public BungeecordCommandsRegistry getCommandsRegistry() {
        return registry;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ProxyServer getServer() {
        return plugin.getProxy();
    }

    public BungeecordPlatformConfiguration getConfiguration() {
        return configuration;
    }

}
