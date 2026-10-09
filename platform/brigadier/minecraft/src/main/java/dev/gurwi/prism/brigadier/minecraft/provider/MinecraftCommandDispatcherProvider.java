package dev.gurwi.prism.brigadier.minecraft.provider;

import dev.gurwi.prism.brigadier.minecraft.BrigadierCompatibilityChecker;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandDispatcher;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class MinecraftCommandDispatcherProvider {

    private static final String PACKAGE = "dev.gurwi.prism.brigadier.minecraft.";
    private static final String CLASS_NAME = "CommandDispatcherProvider";
    private static final String PAPER_COMMANDS_CLASS = "io.papermc.paper.command.brigadier.Commands";

    protected final Plugin plugin;

    protected MinecraftCommandDispatcherProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract MinecraftCommandDispatcher<?> getDispatcher();

    public static @NotNull Optional<MinecraftCommandDispatcherProvider> get(@NotNull Plugin plugin) {
        if (!BrigadierCompatibilityChecker.isSupported()) {
            return Optional.empty();
        }

        ClassLoader loader = MinecraftCommandDispatcherProvider.class.getClassLoader();

        for (String candidate : candidates()) {
            try {
                Class<?> type = Class.forName(PACKAGE + candidate + "." + CLASS_NAME, true, loader);
                Class<? extends MinecraftCommandDispatcherProvider> provider =
                        type.asSubclass(MinecraftCommandDispatcherProvider.class);

                return Optional.of(ReflectionUtil.instantiate(
                        provider,
                        new Class<?>[]{Plugin.class},
                        plugin
                ));
            } catch (ClassNotFoundException e) {
                plugin.getLogger().fine("Brigadier provider not found: " + candidate);
            } catch (ReflectiveOperationException | LinkageError exception) {
                plugin.getLogger().warning("Failed to load Brigadier provider " + candidate + ": " + exception);
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private static @NotNull List<String> candidates() {
        List<String> result = new ArrayList<>();

//        if (hasPaperBrigadierApi()) {
//            result.add("Paper");
//        }

        String[] parts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");

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