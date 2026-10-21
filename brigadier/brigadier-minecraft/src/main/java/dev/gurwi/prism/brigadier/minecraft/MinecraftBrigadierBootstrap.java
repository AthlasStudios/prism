package dev.gurwi.prism.brigadier.minecraft;

import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftCommandDispatcherProvider;
import dev.gurwi.prism.brigadier.minecraft.provider.MinecraftProviderLoader;
import dev.gurwi.prism.bukkit.brigadier.BrigadierBootstrap;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MinecraftBrigadierBootstrap implements BrigadierBootstrap {

    @Override
    public Optional<BukkitCommandRegistrar> create(@NotNull Plugin plugin) {
        if (!BrigadierCompatibilityChecker.isSupported()) return Optional.empty();

        Optional<MinecraftCommandDispatcherProvider> dispatcherProvider = MinecraftProviderLoader.dispatcherProvider(plugin);

        if (dispatcherProvider.isEmpty()) {
            plugin.getLogger().warning(
                    "[prism] Failed to initialize Brigadier. The Minecraft version may be unsupported. Falling back to legacy registration. If the issue persists on a supported version, report it on our Discord."
            );

            return Optional.empty();
        }

        return dispatcherProvider
                .map(provider -> new BrigadierCommandRegistrar(provider.getDispatcher()));
    }

}
