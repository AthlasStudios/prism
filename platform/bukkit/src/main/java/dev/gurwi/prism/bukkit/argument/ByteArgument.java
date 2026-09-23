package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

public class ByteArgument extends BukkitArgument<ByteArgument, Byte> {

    public ByteArgument(String name) {
        super(name);
    }

    @Override
    public Byte parse(@NotNull String value) throws ArgumentParseException {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a byte got " + value);
        }
    }

}
