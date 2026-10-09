package dev.gurwi.prism.brigadier.minecraft.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;

public interface ArgumentResolver<T> {

    T resolve(MinecraftCommandSource source) throws CommandSyntaxException;

}
