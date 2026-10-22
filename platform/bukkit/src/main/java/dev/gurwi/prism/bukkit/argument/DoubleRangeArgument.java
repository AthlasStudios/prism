package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.NumberRangeArgument;

public class DoubleRangeArgument extends NumberRangeArgument<Double> {

    public DoubleRangeArgument(String name) {
        super(name, Double::parseDouble);
    }

}
