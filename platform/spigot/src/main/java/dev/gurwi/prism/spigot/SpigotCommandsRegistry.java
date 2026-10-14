package dev.gurwi.prism.spigot;

import dev.gurwi.prism.brigadier.minecraft.BrigadierCommandRegistrar;
import dev.gurwi.prism.brigadier.minecraft.BrigadierCompatibilityChecker;
import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftProviderLoader;
import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import dev.gurwi.prism.bukkit.registrar.LegacyCommandRegistrar;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class SpigotCommandsRegistry implements BukkitCommandsRegistry<SpigotPlatform> {

    private final SpigotPlatform platform;
    private final BukkitCommandRegistrar registrar;

    public SpigotCommandsRegistry(@NotNull SpigotPlatform platform) {
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
    public SpigotPlatform getPlatform() {
        return platform;
    }

    @Contract(" -> new")
    private @NotNull BukkitCommandRegistrar getRegistrar() {
        try {
            AtomicReference<BukkitCommandRegistrar> registrar = new AtomicReference<>(new LegacyCommandRegistrar(platform.getPlugin()));

            if (!platform.getConfiguration().isBrigadierDisabled() && BrigadierCompatibilityChecker.isSupported()) {
                MinecraftProviderLoader.dispatcherProvider(platform.getPlugin()).ifPresentOrElse(
                        provider -> registrar.set(new BrigadierCommandRegistrar(provider.getDispatcher())),
                        () -> platform.getPlugin().getLogger().warning(
                                "[prism] Failed to initialize Brigadier. The Minecraft version may be unsupported. Falling back to legacy registration. If the issue persists on a supported version, report it on our Discord."
                        )
                );
            }

            return registrar.get();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
