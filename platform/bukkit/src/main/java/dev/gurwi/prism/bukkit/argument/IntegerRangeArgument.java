package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.NumberRangeArgument;

public class IntegerRangeArgument extends NumberRangeArgument<Integer> {

    public IntegerRangeArgument(String name) {
        super(name, Integer::parseInt);
    }

}
