package dev.gurwi.prism.brigadier.minecraft.argument.mapping;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;

@FunctionalInterface
public interface ArgumentValueMapper<Argument, Value, BrigadierValue> {

    Value map(Argument argument, MinecraftCommandSource source, BrigadierValue value) throws CommandSyntaxException;

}
