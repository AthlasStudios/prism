package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;

public class IntegerArgument extends BukkitArgument<IntegerArgument, Integer> {

    public IntegerArgument(String name) {
        super(name);
    }

    @Override
    public Integer parse(String value) throws ArgumentParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(this, value, e.getMessage());
        }
    }

}
