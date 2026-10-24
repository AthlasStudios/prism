package dev.gurwi.prism.bungeecord.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.bungeecord.argument.base.BoundedArgument;
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
    public Float parse(@NotNull CommandSender source, @NotNull String value) throws ArgumentParseException {
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
    public ArgumentType<Float> getArgumentType() {
        return FloatArgumentType.floatArg(min, max);
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
