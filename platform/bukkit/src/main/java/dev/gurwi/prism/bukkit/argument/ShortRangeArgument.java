package dev.gurwi.prism.bukkit.argument;

public class ShortRangeArgument extends NumberRangeArgument<Short> {

    public ShortRangeArgument(String name) {
        super(name, Short::parseShort);
    }

}
