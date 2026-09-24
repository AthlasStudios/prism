package dev.gurwi.prism.bukkit.command.error;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class DefaultCommandExecutionErrorHandler implements LegacyCommandExecutionErrorHandler {

    private final LegacyCommandExecutionErrorHandler consoleHandler = LegacyCommandExecutionErrorHandler.consoleHandler();

    protected DefaultCommandExecutionErrorHandler() {

    }

    @Override
    public void onArgumentsParseError(@NotNull BukkitCommandDefinition command, @NotNull CommandSender sender, @NotNull BukkitArgumentsParseException exception) {
        if (sender instanceof ConsoleCommandSender) {
            consoleHandler.onArgumentsParseError(command, sender, exception);
            return;
        }

        sender.sendMessage(
                ChatColor.RED +
                        switch (exception.getType()) {
                            case INVALID_ARGUMENT -> formatInvalidArgument(command, exception);
                            case MISSING_ARGUMENT -> formatMissingArgument(exception);
                            case UNKNOWN_ARGUMENT -> formatUnknownArgument(exception);
                        }
        );
    }

    private @NotNull String formatInvalidArgument(@NotNull BukkitCommandDefinition command, @NotNull BukkitArgumentsParseException exception) {
        String input = exception.getInput();
        String prefix = "Input: ";

        return "\n"
                + "Invalid argument: " + exception.getMessage()
                + "\n"
                + "Usage: " + command.getUsageString()
                + "\n"
                + underline(
                prefix + input,
                prefix.length() + exception.getStart(),
                prefix.length() + exception.getEnd()
        )
                + "\n";
    }

    private @NotNull String formatMissingArgument(@NotNull BukkitArgumentsParseException exception) {
        return "\n"
                + exception.getMessage()
                + "\n"
                + underline(
                exception.getInput(),
                exception.getStart(),
                exception.getEnd()
        )
                + "\n";
    }

    private @NotNull String formatUnknownArgument(@NotNull BukkitArgumentsParseException exception) {
        return "\n"
                + exception.getMessage()
                + "\n"
                + underline(
                exception.getInput(),
                exception.getStart(),
                exception.getEnd()
        )
                + "\n";
    }

    private @NotNull String underline(@NotNull String input, int start, int end) {
        return input.substring(0, start)
                + ChatColor.DARK_RED
                + ChatColor.UNDERLINE
                + input.substring(start, end)
                + ChatColor.RED
                + input.substring(end);
    }

}