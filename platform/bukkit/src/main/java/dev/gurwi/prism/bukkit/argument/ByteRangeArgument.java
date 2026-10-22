package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.NumberRangeArgument;

public class ByteRangeArgument extends NumberRangeArgument<Byte> {

    public ByteRangeArgument(String name) {
        super(name, Byte::parseByte);
    }

}
