package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.argument.AbstractArgument;

public abstract class BukkitArgument<Impl, T> extends AbstractArgument<Impl, T, BukkitSuggestionsSupplier<T>> {

    public BukkitArgument(String name) {
        super(name);
    }

}
