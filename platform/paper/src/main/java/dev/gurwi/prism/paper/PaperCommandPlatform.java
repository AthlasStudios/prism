package dev.gurwi.prism.paper;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.bukkit.BukkitCommandPlatform;
import dev.gurwi.prism.paper.listener.AsyncTabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PaperCommandPlatform implements BukkitCommandPlatform<PaperCommandsRegistry, PaperCommandPlatformConfiguration> {

    private final Plugin plugin;
    private final PaperCommandPlatformConfiguration configuration;
    private final PaperCommandsRegistry commandsRegistry;

    protected PaperCommandPlatform(@NotNull Plugin plugin, @NotNull PaperCommandPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.commandsRegistry = new PaperCommandsRegistry(this);

        if (!configuration.isAsyncSuggestionsDisabled()) {
            plugin.getServer().getPluginManager().registerEvents(new AsyncTabCompleter(this), plugin);
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull PaperCommandPlatformConfiguration.Builder configure(@NotNull Plugin plugin) {
        return new PaperCommandPlatformConfiguration.Builder(plugin);
    }

    @Override
    public String getIdentifier() {
        return Prism.PAPER_PLATFORM_IDENTIFIER;
    }

    @Override
    public PaperCommandsRegistry getCommandsRegistry() {
        return commandsRegistry;
    }

    @Override
    public PaperCommandPlatformConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

}
