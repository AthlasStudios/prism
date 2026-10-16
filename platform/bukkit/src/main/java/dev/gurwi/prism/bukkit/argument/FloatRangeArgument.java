package dev.gurwi.prism.bukkit.argument;

public class FloatRangeArgument extends NumberRangeArgument<Float> {

    public FloatRangeArgument(String name) {
        super(name, Float::parseFloat);
    }

}
