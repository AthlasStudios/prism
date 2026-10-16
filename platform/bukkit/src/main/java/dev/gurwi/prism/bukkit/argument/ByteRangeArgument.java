package dev.gurwi.prism.bukkit.argument;

public class ByteRangeArgument extends NumberRangeArgument<Byte> {

    public ByteRangeArgument(String name) {
        super(name, Byte::parseByte);
    }

}
