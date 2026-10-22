package dev.gurwi.prism.velocity.argument;

import dev.gurwi.prism.velocity.argument.base.NumberRangeArgument;

public class DoubleRangeArgument extends NumberRangeArgument<Double> {

    public DoubleRangeArgument(String name) {
        super(name, Double::parseDouble);
    }

}
