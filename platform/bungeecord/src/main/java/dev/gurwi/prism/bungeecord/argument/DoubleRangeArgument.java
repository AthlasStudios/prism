package dev.gurwi.prism.bungeecord.argument;

import dev.gurwi.prism.bungeecord.argument.base.NumberRangeArgument;

public class DoubleRangeArgument extends NumberRangeArgument<Double> {

    public DoubleRangeArgument(String name) {
        super(name, Double::parseDouble);
    }

}
