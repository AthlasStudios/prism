package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

public class DoubleArgument extends BukkitArgument<DoubleArgument, Double> {

    public DoubleArgument(String name) {
        super(name);
    }

    @Override
    public Double parse(@NotNull String value) throws ArgumentParseException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a double got " + value);
        }
    }

}
