package dev.gurwi.prism.bukkit.argument;

public class IntegerRangeArgument extends NumberRangeArgument<Integer> {

    public IntegerRangeArgument(String name) {
        super(name, Integer::parseInt);
    }

}
