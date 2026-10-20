package dev.gurwi.prism.paper;

import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.listener.AsyncSuggestionsHandler;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import dev.gurwi.prism.bukkit.registrar.LegacyCommandRegistrar;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public class PaperCommandsRegistry implements BukkitCommandsRegistry<PaperPlatform> {

    private final PaperPlatform platform;
    private final BukkitCommandRegistrar registrar;

    public PaperCommandsRegistry(@NotNull PaperPlatform platform) {
        this.platform = platform;
        this.registrar = getRegistrar();

        platform.getPlugin().getLogger().info(
                "[prism] Command registration initialized using " + registrar.getType().name() + "."
        );
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        if (getRegisteredCommands().contains(definition)) throw new IllegalStateException("Command " + definition.getName() + " is already registered!");
        registrar.register(definition);
    }

    @Override
    public void unregister(BukkitCommandDefinition definition) {
        registrar.unregister(definition);
    }

    @Override
    public @Unmodifiable Set<BukkitCommandDefinition> getRegisteredCommands() {
        return registrar.getRegisteredCommands();
    }

    @Override
    public PaperPlatform getPlatform() {
        return platform;
    }

    @Contract(" -> new")
    private @NotNull BukkitCommandRegistrar getRegistrar() {
        BukkitCommandRegistrar registrar = createRegistrar(platform);

        if (registrar instanceof LegacyCommandRegistrar) {
            platform.getPlugin().getServer().getPluginManager().registerEvents(new AsyncSuggestionsHandler((LegacyCommandRegistrar) registrar), platform.getPlugin());
        }

        return registrar;
    }

}
