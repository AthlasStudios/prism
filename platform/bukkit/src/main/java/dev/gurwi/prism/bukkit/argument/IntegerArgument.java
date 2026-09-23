package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

public class IntegerArgument extends BukkitArgument<IntegerArgument, Integer> {

    public IntegerArgument(String name) {
        super(name);
    }

    @Override
    public Integer parse(@NotNull String value) throws ArgumentParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, "Expected an integer got " + value);
        }
    }

}
