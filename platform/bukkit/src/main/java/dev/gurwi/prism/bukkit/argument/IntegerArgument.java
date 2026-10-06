package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class IntegerArgument extends BukkitArgument<IntegerArgument, Integer> {

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
    public Integer parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
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

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

}
