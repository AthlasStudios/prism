package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

public class ShortArgument extends BukkitArgument<ShortArgument, Short> {

    public ShortArgument(String name) {
        super(name);
    }

    @Override
    public Short parse(@NotNull String value) throws ArgumentParseException {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected a short got " + value);
        }
    }

}
