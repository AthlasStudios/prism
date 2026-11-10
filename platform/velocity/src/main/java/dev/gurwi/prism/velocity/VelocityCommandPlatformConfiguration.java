package dev.gurwi.prism.velocity;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

public class VelocityCommandPlatformConfiguration {

    private VelocityCommandPlatformConfiguration(@NotNull Builder builder) {

    }

    public static class Builder {

        private final Object plugin;
        private final ProxyServer server;

        public Builder(@NotNull Object plugin, ProxyServer server) {
            if (!plugin.getClass().isAnnotationPresent(Plugin.class)) throw new IllegalArgumentException("Plugin must be annotated with @Plugin");

            this.plugin = plugin;
            this.server = server;
        }

        public @NotNull VelocityCommandPlatform init() {
            return new VelocityCommandPlatform(plugin, server, new VelocityCommandPlatformConfiguration(this));
        }

    }

}
