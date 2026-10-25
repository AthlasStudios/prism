package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;

public interface NativeTypeHolder {

    @NotNull ArgumentType<?> getNativeType();

}
