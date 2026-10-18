package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BoundedArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ByteArgument extends BoundedArgument<ByteArgument, Byte> {

    private final byte min;
    private final byte max;

    public ByteArgument(String name, byte min, byte max) {
        super(name);

        this.min = min;
        this.max = max;
    }

    public ByteArgument(String name, byte min) {
        this(name, min, Byte.MAX_VALUE);
    }

    public ByteArgument(String name) {
        this(name, Byte.MIN_VALUE);
    }

    @Override
    public Byte parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            byte parsedValue = Byte.parseByte(value);
            if (parsedValue < min || parsedValue > max) {
                throw new ArgumentParseException(this, value, "Expected a byte between " + min + " and " + max + " got " + value);
            }

            return parsedValue;
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a byte got " + value);
        }
    }

    @Override
    public Byte getMin() {
        return min;
    }

    @Override
    public Byte getMax() {
        return max;
    }


}
