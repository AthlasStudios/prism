package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;

public class StringArgument extends BukkitArgument<StringArgument, String> {

    public StringArgument(String name) {
        super(name);
    }

    @Override
    public String parse(String value) throws ArgumentParseException {
        return value;
    }

}
