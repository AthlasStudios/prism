package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

public class LongArgument extends BukkitArgument<LongArgument, Long> {

    public LongArgument(String name) {
        super(name);
    }

    @Override
    public Long parse(@NotNull String value) throws ArgumentParseException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a long got " + value);
        }
    }

}
