package dev.gurwi.prism.bukkit.argument;

import org.jetbrains.annotations.NotNull;

public class StringArgument extends Argument<StringArgument, String> {

    public StringArgument(String name) {
        super(name);
    }

    @Override
    public boolean validate(String rawArg) {
        return true;
    }

    @Override
    public String parse(String @NotNull [] args, int index) {
        return args[index];
    }

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public StringArgument instance() {
        return this;
    }

}
