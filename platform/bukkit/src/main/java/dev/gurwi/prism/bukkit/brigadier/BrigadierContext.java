package dev.gurwi.prism.bukkit.brigadier;

import dev.gurwi.prism.bukkit.argument.base.ArgumentContext;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrigadierContext<Argument extends BukkitArgument<Argument, ?>> extends ArgumentContext<Argument> {

    private final @Nullable Object nativeType;
    private final boolean nativeSuggestions;

    public BrigadierContext(@NotNull Argument argument, @Nullable Object nativeType, boolean nativeSuggestions) {
        super(argument);

        this.nativeType = nativeType;
        this.nativeSuggestions = nativeSuggestions;
    }

    public @Nullable Object getNativeType() {
        return nativeType;
    }

    public <T> @Nullable T getNativeType(@NotNull Class<T> type) {
        return type.isInstance(nativeType) ? type.cast(nativeType) : null;
    }

    public boolean hasNativeType() {
        return nativeType != null;
    }

    public boolean hasNativeSuggestions() {
        return nativeSuggestions;
    }

}
