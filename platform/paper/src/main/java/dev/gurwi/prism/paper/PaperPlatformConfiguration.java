package dev.gurwi.prism.paper;

import dev.gurwi.prism.bukkit.BukkitPlatformConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PaperPlatformConfiguration extends BukkitPlatformConfiguration<PaperPlatform> {

    private final boolean disableAsyncSuggestions;

    private PaperPlatformConfiguration(@NotNull Builder builder) {
        super(builder);

        this.disableAsyncSuggestions = builder.disableAsyncSuggestions;
    }

    public boolean isAsyncSuggestionsDisabled() {
        return disableAsyncSuggestions;
    }

    public static class Builder extends BukkitPlatformConfiguration.Builder<PaperPlatform, Builder> {

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
        public @NotNull PaperPlatform init() {
            //if (isLegacyRegistrarDisabled() && ...) throw new IllegalStateException("Cannot disable both legacy registrar and brigadier");
            return new PaperPlatform(getPlugin(), new PaperPlatformConfiguration(this));
        }

//        public PaperPlatform init(BootstrapContext context) {
//            return new PaperPlatform(getPlugin(), new PaperPlatformConfiguration(this));
//        }

    }

}