package dev.gurwi.prism.spigot;

import dev.gurwi.prism.bukkit.BukkitPlatformConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatformConfiguration extends BukkitPlatformConfiguration<SpigotPlatform> {

    private SpigotPlatformConfiguration(@NotNull Builder builder) {
        super(builder);
    }

    public static class Builder extends BukkitPlatformConfiguration.Builder<SpigotPlatform, Builder> {

        public Builder(@NotNull Plugin plugin) {
            super(plugin);
        }

        @Override
        protected @NotNull Builder self() {
            return this;
        }

        @Override
        public @NotNull SpigotPlatform init() {
            return new SpigotPlatform(getPlugin(), new SpigotPlatformConfiguration(this));
        }

    }

}