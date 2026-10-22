package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.NumberRangeArgument;

public class LongRangeArgument extends NumberRangeArgument<Long> {

    public LongRangeArgument(String name) {
        super(name, Long::parseLong);
    }

}
