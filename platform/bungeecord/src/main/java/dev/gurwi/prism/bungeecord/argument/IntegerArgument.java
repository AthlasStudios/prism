package dev.gurwi.prism.bungeecord.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.bungeecord.argument.base.BoundedArgument;
import org.jetbrains.annotations.NotNull;

public class IntegerArgument extends BoundedArgument<IntegerArgument, Integer> {

    private final int min;
    private final int max;

    public IntegerArgument(String name, int min, int max) {
        super(name);

        this.min = min;
        this.max = max;
    }

    public IntegerArgument(String name, int min) {
        this(name, min, Integer.MAX_VALUE);
    }

    public IntegerArgument(String name) {
        this(name, Integer.MIN_VALUE);
    }

    @Override
    public Integer parse(@NotNull CommandSender source, @NotNull String value) throws ArgumentParseException {
        try {
            int parsedValue = Integer.parseInt(value);
            if (parsedValue < min || parsedValue > max) {
                throw new ArgumentParseException(this, value, "Expected an integer between " + min + " and " + max + " got " + value);
            }

            return parsedValue;
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected an integer got " + value);
        }
    }

    @Override
    public ArgumentType<Integer> getArgumentType() {
        return IntegerArgumentType.integer(min, max);
    }

    @Override
    public Integer getMin() {
        return min;
    }

    @Override
    public Integer getMax() {
        return max;
    }

}
