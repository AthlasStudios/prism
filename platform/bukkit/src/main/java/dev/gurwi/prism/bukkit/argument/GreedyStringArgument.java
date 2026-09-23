package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

public class GreedyStringArgument extends GreedyArgument<GreedyStringArgument, String> {

    public GreedyStringArgument(String name) {
        super(name);
    }

    @Override
    public String parse(@NotNull String value) throws ArgumentParseException {
        return value;
    }

}
