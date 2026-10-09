package dev.gurwi.prism.brigadier.minecraft.argument;

import dev.gurwi.prism.brigadier.minecraft.argument.mapping.ArgumentMapper;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;

public abstract class BrigadierArgument<Impl extends BrigadierArgument<Impl, T>, T> extends BukkitArgument<Impl, T> implements ArgumentMapper<T, Impl> {

    public BrigadierArgument(String name) {
        super(name);
    }

}
