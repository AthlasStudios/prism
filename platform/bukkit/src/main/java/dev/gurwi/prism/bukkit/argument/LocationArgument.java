package dev.gurwi.prism.bukkit.argument;

public class LocationArgument extends PositionArgument<LocationArgument> {

    public LocationArgument(String name, boolean centerIntegers) {
        super(name, false, false, centerIntegers);
    }

    public LocationArgument(String name) {
        this(name, true);
    }

}