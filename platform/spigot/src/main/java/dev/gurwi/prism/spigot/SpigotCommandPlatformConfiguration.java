package dev.gurwi.prism.spigot;

import dev.gurwi.prism.bukkit.BukkitCommandPlatformConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SpigotCommandPlatformConfiguration extends BukkitCommandPlatformConfiguration<SpigotCommandPlatform> {

    private SpigotCommandPlatformConfiguration(@NotNull Builder builder) {
        super(builder);
    }

    public static class Builder extends BukkitCommandPlatformConfiguration.Builder<SpigotCommandPlatform, Builder> {

        public Builder(@NotNull Plugin plugin) {
            super(plugin);
        }

        @Override
        protected @NotNull Builder self() {
            return this;
        }

        @Override
        public @NotNull SpigotCommandPlatform init() {
            return new SpigotCommandPlatform(getPlugin(), new SpigotCommandPlatformConfiguration(this));
        }

    }

}