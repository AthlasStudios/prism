package dev.gurwi.prism.bukkit;

import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class BukkitPlatformConfiguration<Platform extends BukkitPlatform<?, ?>> {

    private final boolean disableBrigadier;
    private final boolean disableLegacyRegistrar;
    private final boolean registerPermissions;
    private final LegacyCommandExecutionErrorHandler legacyCommandErrorHandle;

    @Contract(pure = true)
    protected BukkitPlatformConfiguration(@NotNull Builder<?, ?> builder) {
        this.disableBrigadier = builder.disableBrigadier;
        this.registerPermissions = builder.registerPermissions;
        this.disableLegacyRegistrar = builder.disableLegacyRegistrar;
        this.legacyCommandErrorHandle = builder.legacyCommandErrorHandle;
    }

    public boolean isBrigadierDisabled() {
        return disableBrigadier;
    }

    public boolean isLegacyRegistrarDisabled() {
        return disableLegacyRegistrar;
    }

    public boolean isPermissionRegistrationEnabled() {
        return registerPermissions;
    }

    @Contract(pure = true)
    public @NotNull Optional<LegacyCommandExecutionErrorHandler> getGlobalLegacyCommandErrorHandler() {
        return Optional.ofNullable(legacyCommandErrorHandle);
    }

    public abstract static class Builder<Platform extends BukkitPlatform<?, ?>, Builder extends BukkitPlatformConfiguration.Builder<Platform, Builder>> {

        private final Plugin plugin;

        private boolean disableBrigadier = false;
        private boolean registerPermissions = true;
        private boolean disableLegacyRegistrar = false;
        private LegacyCommandExecutionErrorHandler legacyCommandErrorHandle = null;

        protected Builder(@NotNull Plugin plugin) {
            this.plugin = plugin;
        }

        protected abstract @NotNull Builder self();

        public @NotNull Builder disableBrigadier() {
            this.disableBrigadier = true;
            return self();
        }

        public @NotNull Builder disableLegacyRegistrar() {
            this.disableLegacyRegistrar = true;
            return self();
        }

        public @NotNull Builder registerPermissions(boolean registerPermissions) {
            this.registerPermissions = registerPermissions;
            return self();
        }

        public @NotNull Builder withGlobalLegacyCommandErrorHandler(LegacyCommandExecutionErrorHandler handler) {
            this.legacyCommandErrorHandle = handler;
            return self();
        }

        public @NotNull Plugin getPlugin() {
            return plugin;
        }

        public abstract @NotNull Platform init();

    }

}