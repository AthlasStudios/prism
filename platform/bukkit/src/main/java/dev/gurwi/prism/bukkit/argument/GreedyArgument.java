package dev.gurwi.prism.bukkit.argument;

public abstract class GreedyArgument<Impl, T> extends BukkitArgument<Impl, T> {

    public GreedyArgument(String name) {
        super(name);
    }

}
