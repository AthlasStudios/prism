package dev.gurwi.prism.bukkit.command.error;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface LegacyCommandExecutionErrorHandler {

    void onArgumentsParseError(@NotNull BukkitCommandDefinition command, @NotNull CommandSender sender, @NotNull BukkitArgumentsParseException exception);

    @Contract(value = " -> new", pure = true)
    static @NotNull LegacyCommandExecutionErrorHandler defaultHandler() {
        return new DefaultCommandExecutionErrorHandler();
    }

}
