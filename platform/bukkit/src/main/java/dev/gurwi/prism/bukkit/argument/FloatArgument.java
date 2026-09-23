package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

public class FloatArgument extends BukkitArgument<FloatArgument, Float> {

    public FloatArgument(String name) {
        super(name);
    }

    @Override
    public Float parse(@NotNull String value) throws ArgumentParseException {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a float got " + value);
        }
    }

}
