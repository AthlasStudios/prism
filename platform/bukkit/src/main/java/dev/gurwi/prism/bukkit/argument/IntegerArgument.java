package dev.gurwi.prism.bukkit.argument;

import org.jetbrains.annotations.NotNull;

public class IntegerArgument extends Argument<IntegerArgument, Integer> {

    public IntegerArgument(String name) {
        super(name);
    }

    @Override
    public boolean validate(String rawArg) {
        try {
            Integer.parseInt(rawArg);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Integer parse(String @NotNull [] args, int index) {
        return Integer.parseInt(args[index]);
    }

    @Override
    public Class<Integer> getPrimitiveType() {
        return Integer.class;
    }

    @Override
    public IntegerArgument instance() {
        return this;
    }

}
