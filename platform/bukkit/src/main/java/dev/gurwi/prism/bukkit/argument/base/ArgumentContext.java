package dev.gurwi.prism.bukkit.argument.base;

public class ArgumentContext<Argument extends BukkitArgument<Argument, ?>> {

    private final Argument argument;

    protected ArgumentContext(Argument argument) {
        this.argument = argument;
    }

    public Argument getArgument() {
        return argument;
    }

}
