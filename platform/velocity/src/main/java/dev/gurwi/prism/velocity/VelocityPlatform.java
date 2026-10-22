package dev.gurwi.prism.velocity;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.gurwi.prism.Prism;
import dev.gurwi.prism.platform.Platform;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class VelocityPlatform implements Platform<VelocityCommandsRegistry> {

    private final Plugin plugin;
    private final ProxyServer server;
    private final VelocityPlatformConfiguration configuration;
    private final VelocityCommandsRegistry registry;

    protected VelocityPlatform(@NotNull Plugin plugin, ProxyServer server, @NotNull VelocityPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.server = server;
        this.configuration = configuration;

        this.registry = new VelocityCommandsRegistry(this);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull VelocityPlatformConfiguration.Builder configure(@NotNull Plugin plugin, ProxyServer server) {
        return new VelocityPlatformConfiguration.Builder(plugin, server);
    }

    @Override
    public String getIdentifier() {
        return Prism.VELOCITY_PLATFORM_IDENTIFIER;
    }

    @Override
    public VelocityCommandsRegistry getCommandsRegistry() {
        return registry;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ProxyServer getServer() {
        return server;
    }

}
