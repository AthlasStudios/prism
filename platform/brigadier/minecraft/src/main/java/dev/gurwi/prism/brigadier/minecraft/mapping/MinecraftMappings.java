package dev.gurwi.prism.brigadier.minecraft.mapping;

import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MinecraftMappings {

    private static final Map<Class<?>, MinecraftArgumentMapping<?>> MAPPINGS = new ConcurrentHashMap<>();

    private MinecraftMappings() {
    }

    public static <A extends BukkitArgument<?, ?>> void register(@NotNull Class<A> type, @NotNull MinecraftArgumentMapping<A> mapping) {
        MAPPINGS.put(type, mapping);
    }

    @SuppressWarnings("unchecked")
    public static @Nullable MinecraftArgumentMapping<BukkitArgument<?, ?>> find(@NotNull Object argument) {
        Class<?> type = argument.getClass();

        while (type != null && type != Object.class) {
            MinecraftArgumentMapping<?> mapping = MAPPINGS.get(type);

            if (mapping != null) {
                return (MinecraftArgumentMapping<BukkitArgument<?, ?>>) mapping;
            }

            type = type.getSuperclass();
        }

        return null;
    }

}