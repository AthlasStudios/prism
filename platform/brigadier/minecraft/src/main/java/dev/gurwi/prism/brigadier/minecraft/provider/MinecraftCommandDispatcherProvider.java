package dev.gurwi.prism.brigadier.minecraft.provider;

import dev.gurwi.prism.brigadier.minecraft.BrigadierCompatibilityChecker;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public abstract class MinecraftCommandDispatcherProvider {

    private static final String CLASS_NAME = "CommandDispatcherProvider";
    private static final String PAPER_COMMANDS_CLASS = "io.papermc.paper.command.brigadier.Commands";

    protected final Plugin plugin;

    protected MinecraftCommandDispatcherProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract MinecraftCommandDispatcher<?> getDispatcher();

    public static @NotNull Optional<MinecraftCommandDispatcherProvider> get(@NotNull Plugin plugin) {
        if (!BrigadierCompatibilityChecker.isSupported()) {
            plugin.getLogger().warning("Brigadier provider: Brigadier classes not found on this server");
            return Optional.empty();
        }

        ClassLoader loader = MinecraftCommandDispatcherProvider.class.getClassLoader();
        String base = basePackage();
        List<String> tried = new ArrayList<>();

        for (String candidate : candidates()) {
            String name = base + candidate + "." + CLASS_NAME;
            plugin.getLogger().fine("Brigadier provider: trying " + name);
            tried.add(name);

            try {
                Class<?> type = Class.forName(name, true, loader);
                Class<? extends MinecraftCommandDispatcherProvider> provider = type.asSubclass(MinecraftCommandDispatcherProvider.class);

                return Optional.of(ReflectionUtil.instantiate(
                        provider,
                        new Class<?>[]{Plugin.class},
                        plugin
                ));
            } catch (ClassNotFoundException ignored) {
            } catch (InvocationTargetException exception) {
                plugin.getLogger().log(Level.WARNING, "Brigadier provider " + name + " failed to initialize", exception.getCause());
            } catch (ReflectiveOperationException | LinkageError exception) {
                plugin.getLogger().log(Level.WARNING, "Brigadier provider " + name + " failed to load", exception);
            }
        }

        plugin.getLogger().warning("Brigadier provider: no usable provider, tried " + tried);

        return Optional.empty();
    }

    private static @NotNull String basePackage() {
        String name = MinecraftCommandDispatcherProvider.class.getPackageName();

        return name.substring(0, name.lastIndexOf('.')) + ".";
    }

    private static @NotNull List<String> candidates() {
        List<String> result = new ArrayList<>();

        String[] parts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");

        if (hasPaperBrigadierApi()) {
            result.add("paper");
        }

        if (parts.length >= 3) {
            result.add("V" + parts[0] + "_" + parts[1] + "_" + parts[2]);
        }

        if (parts.length >= 2) {
            result.add("V" + parts[0] + "_" + parts[1]);
        }

        return result;
    }

    private static boolean hasPaperBrigadierApi() {
        try {
            Class.forName(
                    PAPER_COMMANDS_CLASS,
                    false,
                    MinecraftCommandDispatcherProvider.class.getClassLoader()
            );
            return true;
        } catch (ClassNotFoundException | LinkageError exception) {
            return false;
        }
    }

}