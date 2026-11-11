package dev.gurwi.prism.exception;

import dev.gurwi.prism.argument.AbstractArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ArgumentParseException extends IllegalArgumentException {

    private final AbstractArgument<?, ?, ?, ?> argument;
    private final String input;

    protected ArgumentParseException(@Nullable AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, @NotNull String message) {
        super(message);

        this.argument = argument;
        this.input = input;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull ArgumentParseException panic(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, @NotNull String message) {
        return new Panic(argument, input, message);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull ArgumentParseException message(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, @NotNull String message) {
        return new Message(argument, input, message);
    }

    @Contract("_, _ -> new")
    public static @NotNull ArgumentParseException silent(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String input) {
        return new Silent(argument, input);
    }

    public AbstractArgument<?, ?, ?, ?> getArgument() {
        return argument;
    }

    public String getInput() {
        return input;
    }

    public static class Panic extends ArgumentParseException {

        private Panic(AbstractArgument<?, ?, ?, ?> argument, String input, String message) {
            super(argument, input, message);
        }
    }

    public static class Message extends ArgumentParseException {

        private Message(AbstractArgument<?, ?, ?, ?> argument, String input, String message) {
            super(argument, input, message);
        }
    }

    public static class Silent extends ArgumentParseException {

        private Silent(AbstractArgument<?, ?, ?, ?> argument, String input) {
            super(argument, input, "");
        }

    }

}
