package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.PositionArgument;

public class LocationArgument extends PositionArgument<LocationArgument> {

    public LocationArgument(String name, boolean centerIntegers) {
        super(name, false, false, centerIntegers);
    }

    public LocationArgument(String name) {
        this(name, true);
    }

}