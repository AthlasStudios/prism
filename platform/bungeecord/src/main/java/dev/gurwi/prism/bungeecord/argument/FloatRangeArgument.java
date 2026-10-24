package dev.gurwi.prism.bungeecord.argument;

import dev.gurwi.prism.bungeecord.argument.base.NumberRangeArgument;

public class FloatRangeArgument extends NumberRangeArgument<Float> {

    public FloatRangeArgument(String name) {
        super(name, Float::parseFloat);
    }

}
