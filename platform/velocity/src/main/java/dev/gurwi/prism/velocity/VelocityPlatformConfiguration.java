package dev.gurwi.prism.velocity;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

public class VelocityPlatformConfiguration {

    private VelocityPlatformConfiguration(@NotNull Builder builder) {

    }

    public static class Builder {

        private final Plugin plugin;
        private final ProxyServer server;

        public Builder(Plugin plugin, ProxyServer server) {
            this.plugin = plugin;
            this.server = server;
        }

        public @NotNull VelocityPlatform init() {
            return new VelocityPlatform(plugin, server, new VelocityPlatformConfiguration(this));
        }

    }

}
