package dev.gurwi.prism.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SpigotPlatformConfiguration {

    private final boolean disableBrigadier;
    private final boolean disableLegacyRegistrar;
    private final boolean registerPermissions;

    @Contract(pure = true)
    private SpigotPlatformConfiguration(@NotNull Builder builder) {
        this.disableBrigadier = builder.disableBrigadier;
        this.registerPermissions = builder.registerPermissions;
        this.disableLegacyRegistrar = builder.disableLegacyRegistrar;
    }

    public boolean isBrigadierDisabled() {
        return disableBrigadier;
    }

    public boolean isPermissionRegistrationEnabled() {
        return registerPermissions;
    }

    public static class Builder {

        private final JavaPlugin plugin;

        private boolean disableBrigadier = false;
        private boolean registerPermissions = true;
        private boolean disableLegacyRegistrar = false;

        public Builder(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        public Builder disableBrigadier() {
            this.disableBrigadier = true;
            return this;
        }

        public Builder disableLegacyRegistrar() {
            this.disableLegacyRegistrar = true;
            return this;
        }

        public Builder registerPermissions(boolean registerPermissions) {
            this.registerPermissions = registerPermissions;
            return this;
        }

        public SpigotPlatform init() {
            //if (disableLegacyRegistrar && (disableBrigadier || !PrismBrigadier.isCompatible())) throw new IllegalStateException("Cannot disable both legacy registrar and brigadier");
            return new SpigotPlatform(plugin, new SpigotPlatformConfiguration(this));
        }

    }

}