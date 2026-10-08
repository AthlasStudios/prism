package dev.gurwi.prism.brigadier.minecraft.mapping;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.brigadier.minecraft.MinecraftBrigadierSource;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;

public interface MinecraftArgumentMapping<A extends BukkitArgument<?, ?>> {

    ArgumentType<?> type(A argument);

    Object convert(A argument, MinecraftBrigadierSource source, Object value) throws CommandSyntaxException;

}