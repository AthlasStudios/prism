package dev.gurwi.prism.bukkit.brigadier;

import dev.gurwi.prism.bukkit.argument.base.ArgumentContext;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BrigadierContext<Argument extends BukkitArgument<Argument, ?>> extends ArgumentContext<Argument> {

    private final Object nativeContext;
    private final boolean nativeType;
    private final List<String> nativeSuggestions;

    protected BrigadierContext(Argument argument, Object nativeContext, boolean hasNativeType, List<String> nativeSuggestions) {
        super(argument);

        this.nativeContext = nativeContext;
        this.nativeType = hasNativeType;
        this.nativeSuggestions = nativeSuggestions;
    }

    @SuppressWarnings("unchecked")
    public <T> T getNativeContext() {
        return (T) nativeContext;
    }

    public <T> T getNativeContext(@NotNull Class<T> clazz) {
        return clazz.cast(nativeContext);
    }

    public boolean hasNativeType() {
        return nativeType;
    }

    public List<String> getNativeSuggestions() {
        return nativeSuggestions;
    }

    public boolean hasNativeSuggestions() {
        return !nativeSuggestions.isEmpty();
    }

}
