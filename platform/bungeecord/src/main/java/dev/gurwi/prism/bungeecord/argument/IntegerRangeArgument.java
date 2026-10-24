package dev.gurwi.prism.bungeecord.argument;

import dev.gurwi.prism.bungeecord.argument.base.NumberRangeArgument;

public class IntegerRangeArgument extends NumberRangeArgument<Integer> {

    public IntegerRangeArgument(String name) {
        super(name, Integer::parseInt);
    }

}
