package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.PositionArgument;

public class ColumnLocationArgument extends PositionArgument<ColumnLocationArgument> {

    public ColumnLocationArgument(String name, boolean centerIntegers) {
        super(name, true, false, centerIntegers);
    }

    public ColumnLocationArgument(String name) {
        this(name, true);
    }

}
