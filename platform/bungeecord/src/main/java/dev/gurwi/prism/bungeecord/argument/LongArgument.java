package dev.gurwi.prism.bungeecord.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.bungeecord.argument.base.BoundedArgument;
import org.jetbrains.annotations.NotNull;

public class LongArgument extends BoundedArgument<LongArgument, Long> {

    private final long min;
    private final long max;

    public LongArgument(String name, long min, long max) {
        super(name);

        this.min = min;
        this.max = max;
    }

    public LongArgument(String name, long min) {
        this(name, min, Long.MAX_VALUE);
    }

    public LongArgument(String name) {
        this(name, Long.MIN_VALUE);
    }

    @Override
    public Long parse(@NotNull CommandSender source, @NotNull String value) throws ArgumentParseException {
        try {
            long parsedValue = Long.parseLong(value);
            if (parsedValue < min || parsedValue > max) {
                throw new ArgumentParseException(this, value, "Expected a long between " + min + " and " + max + " got " + value);
            }

            return parsedValue;
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a long got " + value);
        }
    }

    @Override
    public ArgumentType<Long> getArgumentType() {
        return LongArgumentType.longArg(min, max);
    }

    @Override
    public Long getMin() {
        return min;
    }

    @Override
    public Long getMax() {
        return max;
    }

}
