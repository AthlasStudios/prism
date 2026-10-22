package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.NumberRangeArgument;

public class ShortRangeArgument extends NumberRangeArgument<Short> {

    public ShortRangeArgument(String name) {
        super(name, Short::parseShort);
    }

}
