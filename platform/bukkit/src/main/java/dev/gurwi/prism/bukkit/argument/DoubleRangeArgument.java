package dev.gurwi.prism.bukkit.argument;

public class DoubleRangeArgument extends NumberRangeArgument<Double> {

    public DoubleRangeArgument(String name) {
        super(name, Double::parseDouble);
    }

}
