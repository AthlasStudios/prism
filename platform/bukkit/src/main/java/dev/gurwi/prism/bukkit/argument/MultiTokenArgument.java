package dev.gurwi.prism.bukkit.argument;

public abstract class MultiTokenArgument<Impl, T> extends BukkitArgument<Impl, T> {

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
