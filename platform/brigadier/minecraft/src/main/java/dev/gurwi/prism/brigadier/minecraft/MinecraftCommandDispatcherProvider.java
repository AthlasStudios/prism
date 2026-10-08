package dev.gurwi.prism.brigadier.minecraft;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.CommandDispatcherProvider;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface MinecraftCommandDispatcherProvider extends CommandDispatcherProvider<MinecraftBrigadierSource> {

    String PACKAGE = "dev.gurwi.prism.brigadier.minecraft.";
    String CLASS_NAME = "BrigadierDispatcherProvider";

    void register(LiteralCommandNode<MinecraftBrigadierSource> node, String description, List<String> aliases);

    static @NotNull Optional<MinecraftCommandDispatcherProvider> get(@NotNull Plugin plugin) {
        if (!BrigadierCompatibilityChecker.isSupported()) {
            return Optional.empty();
        }

        ClassLoader loader = MinecraftCommandDispatcherProvider.class.getClassLoader();

        for (String candidate : candidates()) {
            try {
                Class<?> type = Class.forName(PACKAGE + candidate + "." + CLASS_NAME, true, loader);
                Class<? extends MinecraftCommandDispatcherProvider> provider = type.asSubclass(MinecraftCommandDispatcherProvider.class);

                return Optional.of(ReflectionUtil.instantiate(provider, new Class<?>[]{Plugin.class}, plugin));
            } catch (ClassNotFoundException ignored) {
                plugin.getLogger().warning("Failed to load Brigadier provider " + candidate + ": " + ignored);
            } catch (ReflectiveOperationException | LinkageError exception) {
                plugin.getLogger().warning("Failed to load Brigadier provider " + candidate + ": " + exception);
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private static @NotNull List<String> candidates() {
        String[] parts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        List<String> result = new ArrayList<>();

        if (parts.length >= 3) {
            result.add("V" + parts[0] + "_" + parts[1] + "_" + parts[2]);
        }

        if (parts.length >= 2) {
            result.add("V" + parts[0] + "_" + parts[1]);
        }

        return result;
    }

}