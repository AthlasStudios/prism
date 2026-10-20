package dev.gurwi.prism.bukkit;

import dev.gurwi.prism.bukkit.brigadier.BrigadierBootstrap;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import dev.gurwi.prism.bukkit.registrar.LegacyCommandRegistrar;
import dev.gurwi.prism.platform.CommandsRegistry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public interface BukkitCommandsRegistry<Platform extends BukkitPlatform<? extends BukkitCommandsRegistry<Platform>, ?>> extends CommandsRegistry<Platform, BukkitCommandDefinition> {

    default @NotNull BukkitCommandRegistrar createRegistrar(@NotNull Platform platform) {
        try {
            if (!platform.getConfiguration().isBrigadierDisabled()) {
                Optional<BukkitCommandRegistrar> brigadier = tryBrigadier(platform.getPlugin());

                if (brigadier.isPresent()) {
                    return brigadier.get();
                }
            }

            return new LegacyCommandRegistrar(platform.getPlugin());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default Optional<BukkitCommandRegistrar> tryBrigadier(Plugin plugin) {
        if (Runtime.version().feature() < 16) return Optional.empty();

        try {
            String packageName = BrigadierBootstrap.class.getPackageName();
            String suffix = "bukkit.brigadier";

            if (!packageName.endsWith(suffix)) {
                throw new IllegalStateException("Unexpected BrigadierBootstrap package: " + packageName);
            }

            String root = packageName.substring(0, packageName.length() - suffix.length());

            Class<?> type = Class.forName(
                    root + "brigadier.minecraft.MinecraftBrigadierBootstrap",
                    true,
                    BrigadierBootstrap.class.getClassLoader()
            );

            BrigadierBootstrap bootstrap = (BrigadierBootstrap) type.getDeclaredConstructor().newInstance();

            return bootstrap.create(plugin);
        } catch (ClassNotFoundException | LinkageError e) {
            return Optional.empty();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
