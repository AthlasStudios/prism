package dev.gurwi.prism.bukkit.argument;

public class ColumnLocationArgument extends PositionArgument<ColumnLocationArgument> {

    public ColumnLocationArgument(String name, boolean centerIntegers) {
        super(name, true, false, centerIntegers);
    }

    public ColumnLocationArgument(String name) {
        this(name, true);
    }

}
