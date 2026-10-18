package dev.gurwi.prism.paper;

import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PaperPlatformConfiguration {

    private final boolean disableBrigadier;
    private final boolean disableLegacyRegistrar;
    private final boolean disableAsyncSuggestions;
    private final boolean registerPermissions;
    private final LegacyCommandExecutionErrorHandler legacyCommandErrorHandle;

    @Contract(pure = true)
    private PaperPlatformConfiguration(@NotNull Builder builder) {
        this.disableBrigadier = builder.disableBrigadier;
        this.disableLegacyRegistrar = builder.disableLegacyRegistrar;
        this.disableAsyncSuggestions = builder.disableAsyncSuggestions;
        this.registerPermissions = builder.registerPermissions;
        this.legacyCommandErrorHandle = builder.legacyCommandErrorHandle;
    }

    public boolean isBrigadierDisabled() {
        return disableBrigadier;
    }

    public boolean isLegacyRegistrarDisabled() {
        return disableLegacyRegistrar;
    }

    public boolean isAsyncSuggestionsDisabled() {
        return disableAsyncSuggestions;
    }

    public boolean isPermissionRegistrationEnabled() {
        return registerPermissions;
    }

    @Contract(pure = true)
    public @NotNull Optional<LegacyCommandExecutionErrorHandler> getGlobalLegacyCommandErrorHandler() {
        return Optional.ofNullable(legacyCommandErrorHandle);
    }

    public static class Builder {

        private final JavaPlugin plugin;

        private boolean disableBrigadier = false;
        private boolean disableLegacyRegistrar = false;
        private boolean disableAsyncSuggestions = false;
        private boolean registerPermissions = true;
        private LegacyCommandExecutionErrorHandler legacyCommandErrorHandle = null;

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

        public Builder disableAsyncSuggestions() {
            this.disableAsyncSuggestions = true;
            return this;
        }

        public Builder registerPermissions(boolean registerPermissions) {
            this.registerPermissions = registerPermissions;
            return this;
        }

        public Builder withGlobalLegacyCommandErrorHandler(LegacyCommandExecutionErrorHandler legacyCommandErrorHandle) {
            this.legacyCommandErrorHandle = legacyCommandErrorHandle;
            return this;
        }

        public PaperPlatform init() {
            //if (disableLegacyRegistrar && (disableBrigadier || !PrismBrigadier.isCompatible())) throw new IllegalStateException("Cannot disable both legacy registrar and brigadier");
            return new PaperPlatform(plugin, new PaperPlatformConfiguration(this));
        }

//        public PaperPlatform init(BootstrapContext context) {
//            return new PaperPlatform(plugin, new PaperPlatformConfiguration(this));
//        }

    }

}