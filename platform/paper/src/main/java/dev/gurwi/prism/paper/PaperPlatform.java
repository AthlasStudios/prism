package dev.gurwi.prism.paper;

import dev.gurwi.prism.Prism;
import dev.gurwi.prism.bukkit.BukkitPlatform;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PaperPlatform implements BukkitPlatform<PaperCommandsRegistry> {

    private final JavaPlugin plugin;
    private final PaperPlatformConfiguration configuration;
    private final PaperCommandsRegistry commandsRegistry;

    protected PaperPlatform(JavaPlugin plugin, PaperPlatformConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.commandsRegistry = new PaperCommandsRegistry(this);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull PaperPlatformConfiguration.Builder configure(JavaPlugin plugin) {
        return new PaperPlatformConfiguration.Builder(plugin);
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String getIdentifier() {
        return Prism.PAPER_PLATFORM_IDENTIFIER;
    }

    @Override
    public PaperCommandsRegistry getCommandsRegistry() {
        return commandsRegistry;
    }

}
