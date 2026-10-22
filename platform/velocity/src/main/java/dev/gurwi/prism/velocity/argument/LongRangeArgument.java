package dev.gurwi.prism.velocity.argument;

import dev.gurwi.prism.velocity.argument.base.NumberRangeArgument;

public class LongRangeArgument extends NumberRangeArgument<Long> {

    public LongRangeArgument(String name) {
        super(name, Long::parseLong);
    }

}
