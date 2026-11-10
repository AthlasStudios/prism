package dev.gurwi.prism.bungeecord;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.platform.CommandPlatform;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BungeecordCommandPlatform implements CommandPlatform<BungeecordCommandsRegistry> {

    private final Plugin plugin;
    private final BungeecordCommandPlatformConfiguration configuration;
    private final BungeecordCommandsRegistry registry;

    protected BungeecordCommandPlatform(@NotNull Plugin plugin, @NotNull BungeecordCommandPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;

        this.registry = new BungeecordCommandsRegistry(this);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull BungeecordCommandPlatformConfiguration.Builder configure(@NotNull Plugin plugin) {
        return new BungeecordCommandPlatformConfiguration.Builder(plugin);
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

    public BungeecordCommandPlatformConfiguration getConfiguration() {
        return configuration;
    }

}
