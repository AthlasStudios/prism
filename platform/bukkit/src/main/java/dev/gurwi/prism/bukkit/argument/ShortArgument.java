package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ShortArgument extends BukkitArgument<ShortArgument, Short> {

    private final short min;
    private final short max;

    public ShortArgument(String name, short min, short max) {
        super(name);

        this.min = min;
        this.max = max;
    }

    public ShortArgument(String name, short min) {
        this(name, min, Short.MAX_VALUE);
    }

    public ShortArgument(String name) {
        this(name, Short.MIN_VALUE);
    }

    @Override
    public Short parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
        try {
            short parsedValue = Short.parseShort(value);
            if (parsedValue < min || parsedValue > max) {
                throw new ArgumentParseException(this, value, "Expected a short between " + min + " and " + max + " got " + value);
            }

            return parsedValue;
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a short got " + value);
        }
    }

}
