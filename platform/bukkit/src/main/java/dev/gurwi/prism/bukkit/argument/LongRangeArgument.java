package dev.gurwi.prism.bukkit.argument;

public class LongRangeArgument extends NumberRangeArgument<Long> {

    public LongRangeArgument(String name) {
        super(name, Long::parseLong);
    }

}
