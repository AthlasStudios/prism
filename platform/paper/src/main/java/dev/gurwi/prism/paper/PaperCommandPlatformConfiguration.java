package dev.gurwi.prism.paper;

import dev.gurwi.prism.bukkit.BukkitCommandPlatformConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PaperCommandPlatformConfiguration extends BukkitCommandPlatformConfiguration<PaperCommandPlatform> {

    private final boolean disableAsyncSuggestions;

    private PaperCommandPlatformConfiguration(@NotNull Builder builder) {
        super(builder);

        this.disableAsyncSuggestions = builder.disableAsyncSuggestions;
    }

    public boolean isAsyncSuggestionsDisabled() {
        return disableAsyncSuggestions;
    }

    public static class Builder extends BukkitCommandPlatformConfiguration.Builder<PaperCommandPlatform, Builder> {

        private boolean disableAsyncSuggestions = false;

        public Builder(@NotNull Plugin plugin) {
            super(plugin);
        }

        public @NotNull Builder disableAsyncSuggestions() {
            this.disableAsyncSuggestions = true;
            return this;
        }

        @Override
        protected @NotNull Builder self() {
            return this;
        }

        @Override
        public @NotNull PaperCommandPlatform init() {
            //if (isLegacyRegistrarDisabled() && ...) throw new IllegalStateException("Cannot disable both legacy registrar and brigadier");
            return new PaperCommandPlatform(getPlugin(), new PaperCommandPlatformConfiguration(this));
        }

//        public PaperCommandPlatform init(BootstrapContext context) {
//            return new PaperCommandPlatform(getPlugin(), new PaperCommandPlatformConfiguration(this));
//        }

    }

}