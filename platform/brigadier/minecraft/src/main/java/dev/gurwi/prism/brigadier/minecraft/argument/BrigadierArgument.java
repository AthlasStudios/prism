package dev.gurwi.prism.brigadier.minecraft.argument;

import dev.gurwi.prism.brigadier.minecraft.argument.mapping.ArgumentMapper;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;

public abstract class BrigadierArgument<Impl extends BrigadierArgument<Impl, T, BrigadierType>, T, BrigadierType> extends BukkitArgument<Impl, T> implements ArgumentMapper<T, BrigadierType, Impl> {

    public BrigadierArgument(String name) {
        super(name);
    }

}
