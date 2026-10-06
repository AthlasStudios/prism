package dev.gurwi.prism.paper.mapping;

import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PaperMappings {

    private static final Map<Class<?>, PaperMapping<?>> MAPPINGS = new ConcurrentHashMap<>();

    private PaperMappings() {
    }

    public static <A extends BukkitArgument<?, ?>> void register(@NotNull Class<A> type, @NotNull PaperMapping<A> mapping) {
        MAPPINGS.put(type, mapping);
    }

    @SuppressWarnings("unchecked")
    public static @Nullable PaperMapping<BukkitArgument<?, ?>> find(@NotNull Object argument) {
        Class<?> type = argument.getClass();

        while (type != null && type != Object.class) {
            PaperMapping<?> mapping = MAPPINGS.get(type);

            if (mapping != null) {
                return (PaperMapping<BukkitArgument<?, ?>>) mapping;
            }

            type = type.getSuperclass();
        }

        return null;
    }

}