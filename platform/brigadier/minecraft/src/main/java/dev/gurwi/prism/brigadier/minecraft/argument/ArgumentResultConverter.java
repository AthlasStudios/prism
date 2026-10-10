package dev.gurwi.prism.brigadier.minecraft.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface ArgumentResultConverter<Native, Argument> {

    Argument apply(Native aNative) throws CommandSyntaxException;

}
