package dev.gurwi.prism.bukkit.argument.base;

public abstract class BoundedArgument<Impl extends BoundedArgument<Impl, T>, T> extends BukkitArgument<Impl, T> {

    public BoundedArgument(String name) {
        super(name);
    }

    abstract public T getMin();

    abstract public T getMax();

}
