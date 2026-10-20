package dev.gurwi.prism.paper;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.bukkit.BukkitPlatform;
import dev.gurwi.prism.paper.listener.AsyncTabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PaperPlatform implements BukkitPlatform<PaperCommandsRegistry, PaperPlatformConfiguration> {

    private final Plugin plugin;
    private final PaperPlatformConfiguration configuration;
    private final PaperCommandsRegistry commandsRegistry;

    protected PaperPlatform(@NotNull Plugin plugin, @NotNull PaperPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.commandsRegistry = new PaperCommandsRegistry(this);

        if (!configuration.isAsyncSuggestionsDisabled()) {
            plugin.getServer().getPluginManager().registerEvents(new AsyncTabCompleter(this), plugin);
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull PaperPlatformConfiguration.Builder configure(JavaPlugin plugin) {
        return new PaperPlatformConfiguration.Builder(plugin);
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
    public PaperPlatformConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

}
