package dev.gurwi.prism.paper;

import dev.gurwi.prism.brigadier.minecraft.BrigadierCommandRegistrar;
import dev.gurwi.prism.brigadier.minecraft.BrigadierCompatibilityChecker;
import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftProviderLoader;
import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.listener.AsyncSuggestionsHandler;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import dev.gurwi.prism.bukkit.registrar.LegacyCommandRegistrar;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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
        JavaPlugin plugin = platform.getPlugin();

        try {
            AtomicReference<BukkitCommandRegistrar> registrar = new AtomicReference<>(new LegacyCommandRegistrar(plugin));

            if (!platform.getConfiguration().isBrigadierDisabled() && BrigadierCompatibilityChecker.isSupported()) {
                MinecraftProviderLoader.dispatcherProvider(plugin).ifPresentOrElse(
                        provider -> registrar.set(new BrigadierCommandRegistrar(provider.getDispatcher())),
                        () -> plugin.getLogger().warning(
                                "[prism] Failed to initialize Brigadier. The Minecraft version may be unsupported. Falling back to legacy registration. If the issue persists on a supported version, report it on our Discord."
                        )
                );
            }

            if (registrar.get() instanceof LegacyCommandRegistrar legacyCommandRegistrar) {
                plugin.getServer().getPluginManager().registerEvents(new AsyncSuggestionsHandler(legacyCommandRegistrar), plugin);
            }

            return registrar.get();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
