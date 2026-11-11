package dev.gurwi.prism.bukkit.command.error;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentParseException;
import dev.gurwi.prism.exception.CommandExecutionException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface LegacyCommandExecutionErrorHandler {

    void onArgumentsParseError(@NotNull BukkitCommandDefinition command, @NotNull CommandSender sender, @NotNull BukkitArgumentParseException exception);

    void onCommandExecutionError(@NotNull BukkitCommandDefinition command, @NotNull CommandSender sender, @NotNull CommandExecutionException exception);

    @Contract(value = " -> new", pure = true)
    static @NotNull LegacyCommandExecutionErrorHandler defaultHandler() {
        return new DefaultCommandExecutionErrorHandler();
    }

    @Contract(value = " -> new", pure = true)
    static @NotNull LegacyCommandExecutionErrorHandler consoleHandler() {
        return new ConsoleCommandExecutionErrorHandler();
    }

}
