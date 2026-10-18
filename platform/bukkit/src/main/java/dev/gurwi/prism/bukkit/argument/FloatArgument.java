package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.BoundedArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FloatArgument extends BoundedArgument<FloatArgument, Float> {

    private final float min;
    private final float max;

    public FloatArgument(String name, float min, float max) {
        super(name);

        this.min = min;
        this.max = max;
    }

    public FloatArgument(String name, float min) {
        this(name, min, Float.MAX_VALUE);
    }

    public FloatArgument(String name) {
        this(name, Float.MIN_VALUE);
    }

    @Override
    public Float parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            float parsedValue = Float.parseFloat(value);
            if (parsedValue < min || parsedValue > max) {
                throw new ArgumentParseException(this, value, "Expected a float between " + min + " and " + max + " got " + value);
            }

            return parsedValue;
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a float got " + value);
        }
    }

    @Override
    public Float getMin() {
        return min;
    }

    @Override
    public Float getMax() {
        return max;
    }

}
