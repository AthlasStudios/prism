package dev.gurwi.prism.exception;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class CommandExecutionException extends RuntimeException {

    private CommandExecutionException(String message) {
        super(message);
    }

    @Contract("_ -> new")
    public static @NotNull CommandExecutionException panic(String message) {
        return new Panic(message);
    }

    public static @NotNull CommandExecutionException panic() {
        return panic("");
    }

    @Contract("_ -> new")
    public static @NotNull CommandExecutionException message(String message) {
        return new Message(message);
    }

    public static @NotNull CommandExecutionException silent() {
        return Silent.INSTANCE;
    }

    public static class Panic extends CommandExecutionException {

        private Panic(String message) {
            super(message);
        }

    }

    public static class Message extends CommandExecutionException {

        private Message(String message) {
            super(message);
        }

    }

    public static class Silent extends CommandExecutionException {

        private static final Silent INSTANCE = new Silent();

        private Silent() {
            super("");
        }

    }

}
