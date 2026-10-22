package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.NumberRangeArgument;

public class FloatRangeArgument extends NumberRangeArgument<Float> {

    public FloatRangeArgument(String name) {
        super(name, Float::parseFloat);
    }

}
