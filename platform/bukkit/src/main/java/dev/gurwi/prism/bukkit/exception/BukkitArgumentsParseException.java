package dev.gurwi.prism.bukkit.exception;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BukkitArgumentsParseException extends IllegalArgumentException {

    public enum Type {
        INVALID_ARGUMENT, MISSING_ARGUMENT, UNKNOWN_ARGUMENT
    }

    private final Type type;
    private final AbstractArgument<?, ?, ?, ?> argument;
    private final String input;
    private final int start;
    private final int end;

    private BukkitArgumentsParseException(Type type, AbstractArgument<?, ?, ?, ?> argument, String input, int start, int end, String message, Throwable cause) {
        super(message, cause);

        this.type = type;
        this.argument = argument;
        this.input = input;
        this.start = start;
        this.end = end;
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull BukkitArgumentsParseException invalid(ArgumentParseException cause, String input, int start, int end) {
        return new BukkitArgumentsParseException(Type.INVALID_ARGUMENT, cause.getArgument(), input, start, end, cause.getMessage(), cause);
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull BukkitArgumentsParseException missing(AbstractArgument<?, ?, ?, ?> argument, String usage, int start, int end) {
        return new BukkitArgumentsParseException(Type.MISSING_ARGUMENT, argument, usage, start, end, "Missing argument: <" + argument.getName() + ">", null);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull BukkitArgumentsParseException unknown(String input, int start, int end) {
        return new BukkitArgumentsParseException(Type.UNKNOWN_ARGUMENT, null, input, start, end, "Unknown argument", null);
    }

    public Type getType() {
        return type;
    }

    public AbstractArgument<?, ?, ?, ?> getArgument() {
        return argument;
    }

    public String getInput() {
        return input;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

}