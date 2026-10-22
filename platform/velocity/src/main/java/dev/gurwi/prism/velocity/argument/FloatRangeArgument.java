package dev.gurwi.prism.velocity.argument;

import dev.gurwi.prism.velocity.argument.base.NumberRangeArgument;

public class FloatRangeArgument extends NumberRangeArgument<Float> {

    public FloatRangeArgument(String name) {
        super(name, Float::parseFloat);
    }

}
