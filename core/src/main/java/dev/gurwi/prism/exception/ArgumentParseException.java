package dev.gurwi.prism.exception;

import dev.gurwi.prism.argument.AbstractArgument;

public class ArgumentParseException extends IllegalArgumentException {

    private final AbstractArgument<?, ?> argument;
    private final String input;

    public ArgumentParseException(AbstractArgument<?, ?> argument, String input, String message) {
        super(message);

        this.argument = argument;
        this.input = input;
    }

    public AbstractArgument<?, ?> getArgument() {
        return argument;
    }

    public String getInput() {
        return input;
    }

}
