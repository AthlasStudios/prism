package dev.gurwi.prism.brigadier.minecraft.argument.mapping;

import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;

@FunctionalInterface
public interface ArgumentValueMapper<Item, T> {

    T map(Item item, MinecraftCommandSource source, T value);

}
