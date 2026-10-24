package dev.gurwi.prism.bungeecord.argument;

import dev.gurwi.prism.bungeecord.argument.base.NumberRangeArgument;

public class LongRangeArgument extends NumberRangeArgument<Long> {

    public LongRangeArgument(String name) {
        super(name, Long::parseLong);
    }

}
