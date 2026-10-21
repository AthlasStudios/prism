package dev.gurwi.prism.velocity;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.gurwi.prism.Prism;
import dev.gurwi.prism.platform.Platform;
import org.jetbrains.annotations.NotNull;

public class VelocityPlatform implements Platform<VelocityCommandsRegistry> {

    private final Plugin plugin;
    private final ProxyServer server;
    private final VelocityCommandsRegistry registry;

    public VelocityPlatform(@NotNull Plugin plugin, ProxyServer server) {
        this.plugin = plugin;
        this.server = server;

        this.registry = new VelocityCommandsRegistry(this);
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
