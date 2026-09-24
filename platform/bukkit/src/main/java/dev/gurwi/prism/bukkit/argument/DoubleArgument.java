package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DoubleArgument extends BukkitArgument<DoubleArgument, Double> {

    private final double min;
    private final double max;

    public DoubleArgument(String name, double min, double max) {
        super(name);

        this.min = min;
        this.max = max;
    }

    public DoubleArgument(String name, double min) {
        this(name, min, Double.MAX_VALUE);
    }

    public DoubleArgument(String name) {
        this(name, Double.MIN_VALUE);
    }

    @Override
    public Double parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            double parsedValue = Double.parseDouble(value);
            if (parsedValue < min || parsedValue > max) {
                throw new ArgumentParseException(this, value, "Expected a double between " + min + " and " + max + " got " + value);
            }

            return parsedValue;
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a double got " + value);
        }
    }

}
