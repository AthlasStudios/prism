package dev.gurwi.prism.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BungeecordPlatformConfiguration {

    private final boolean commandTreeInjection;

    private BungeecordPlatformConfiguration(@NotNull Builder builder) {
        this.commandTreeInjection = builder.commandTreeInjection;
    }

    public boolean isCommandTreeInjectionEnabled() {
        return commandTreeInjection;
    }

    public static class Builder {

        private final Plugin plugin;
        private boolean commandTreeInjection = true;

        public Builder(@NotNull Plugin plugin) {
            this.plugin = plugin;
        }

        public @NotNull Builder disableCommandTreeInjection() {
            this.commandTreeInjection = false;
            return this;
        }

        public @NotNull BungeecordPlatform init() {
            return new BungeecordPlatform(plugin, new BungeecordPlatformConfiguration(this));
        }

    }

}
