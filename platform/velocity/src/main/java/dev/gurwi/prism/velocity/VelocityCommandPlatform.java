package dev.gurwi.prism.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.gurwi.prism.Prism;
import dev.gurwi.prism.platform.CommandPlatform;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class VelocityCommandPlatform implements CommandPlatform<VelocityCommandsRegistry> {

    private final Object plugin;
    private final ProxyServer server;
    private final VelocityCommandPlatformConfiguration configuration;
    private final VelocityCommandsRegistry registry;

    protected VelocityCommandPlatform(@NotNull Object plugin, @NotNull ProxyServer server, @NotNull VelocityCommandPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.server = server;
        this.configuration = configuration;

        this.registry = new VelocityCommandsRegistry(this);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull VelocityCommandPlatformConfiguration.Builder configure(@NotNull Object plugin, @NotNull ProxyServer server) {
        return new VelocityCommandPlatformConfiguration.Builder(plugin, server);
    }

    @Override
    public String getIdentifier() {
        return Prism.VELOCITY_PLATFORM_IDENTIFIER;
    }

    @Override
    public VelocityCommandsRegistry getCommandsRegistry() {
        return registry;
    }

    public Object getPlugin() {
        return plugin;
    }

    public ProxyServer getServer() {
        return server;
    }

}