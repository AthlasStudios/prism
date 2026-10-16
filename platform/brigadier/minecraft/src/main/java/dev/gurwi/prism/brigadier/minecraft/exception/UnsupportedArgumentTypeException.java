package dev.gurwi.prism.brigadier.minecraft.exception;

public class UnsupportedArgumentTypeException extends UnsupportedOperationException {

    private final String argument;

    public UnsupportedArgumentTypeException(String argument) {
        super("Argument type '" + argument + "' is not supported on this server version");
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
