package dev.gurwi.prism.velocity.argument;

import dev.gurwi.prism.velocity.argument.base.NumberRangeArgument;

public class IntegerRangeArgument extends NumberRangeArgument<Integer> {

    public IntegerRangeArgument(String name) {
        super(name, Integer::parseInt);
    }

}
