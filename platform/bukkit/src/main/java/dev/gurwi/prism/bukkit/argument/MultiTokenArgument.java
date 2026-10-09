package dev.gurwi.prism.bukkit.argument;

public abstract class MultiTokenArgument<Impl extends MultiTokenArgument<Impl, T>, T> extends BukkitArgument<Impl, T> {

    private final int consumption;

    public MultiTokenArgument(String name, int consumption) {
        super(name);

        if (consumption < 1) {
            throw new IllegalArgumentException("An argument must consume at least one raw argument.");
        }

        this.consumption = consumption;
    }

    public int getConsumption() {
        return consumption;
    }

}
