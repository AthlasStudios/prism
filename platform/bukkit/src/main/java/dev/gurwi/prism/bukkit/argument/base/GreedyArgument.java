package dev.gurwi.prism.bukkit.argument.base;

public abstract class GreedyArgument<Impl extends GreedyArgument<Impl, T>, T> extends BukkitArgument<Impl, T> {

    public GreedyArgument(String name) {
        super(name);
    }

}
