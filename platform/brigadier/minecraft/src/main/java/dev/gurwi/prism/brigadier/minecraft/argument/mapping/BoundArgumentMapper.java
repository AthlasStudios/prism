package dev.gurwi.prism.brigadier.minecraft.argument.mapping;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;

public interface BoundArgumentMapper<T> {

    ArgumentType<?> type();

    T read(CommandContext<MinecraftCommandSource> context, String node, MinecraftCommandSource source) throws CommandSyntaxException;

}
