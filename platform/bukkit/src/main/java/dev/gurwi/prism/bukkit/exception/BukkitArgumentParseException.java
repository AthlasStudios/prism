package dev.gurwi.prism.bukkit.exception;

import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BukkitArgumentParseException extends ArgumentParseException {

    private final int start;
    private final int end;

    private BukkitArgumentParseException(@Nullable AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end, @NotNull String message) {
        super(argument, input, message);

        this.start = start;
        this.end = end;
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull BukkitArgumentParseException invalid(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end, String message) {
        return new Invalid(argument, input, start, end, message);
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull BukkitArgumentParseException missing(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end) {
        return new Missing(argument, input, start, end, "Missing argument: <" + argument.getName() + ">");
    }

    @Contract("_, _, _ -> new")
    public static @NotNull BukkitArgumentParseException unknown(@NotNull String input, int start, int end) {
        return new Unknown(null, input, start, end, "Unknown argument");
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull BukkitArgumentParseException silent(@Nullable AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end) {
        return new Silent(argument, input, start, end);
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull BukkitArgumentParseException message(@Nullable AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end, @NotNull String message) {
        return new Message(argument, input, start, end, message);
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull BukkitArgumentParseException panic(@Nullable AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end, @NotNull String message) {
        return new Panic(argument, input, start, end, message);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public static class Invalid extends BukkitArgumentParseException {

        private Invalid(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end, String message) {
            super(argument, input, start, end, message);
        }

    }

    public static class Missing extends BukkitArgumentParseException {

        private Missing(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end, @NotNull String message) {
            super(argument, input, start, end, message);
        }
    }

    public static class Unknown extends BukkitArgumentParseException {

        private Unknown(@Nullable AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end, @NotNull String message) {
            super(argument, input, start, end, message);
        }
    }

    public static class Silent extends BukkitArgumentParseException {

        private Silent(@Nullable AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end) {
            super(argument, input, start, end, "");
        }
    }

    public static class Message extends BukkitArgumentParseException {

        private Message(@Nullable AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end, @NotNull String message) {
            super(argument, input, start, end, message);
        }
    }

    public static class Panic extends BukkitArgumentParseException {

        private Panic(@Nullable AbstractArgument<?, ?, ?, ?> argument, @NotNull String input, int start, int end, @NotNull String message) {
            super(argument, input, start, end, message);
        }
    }

}