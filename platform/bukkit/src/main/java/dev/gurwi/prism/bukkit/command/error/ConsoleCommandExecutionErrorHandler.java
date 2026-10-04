package dev.gurwi.prism.bukkit.command.error;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommandExecutionErrorHandler implements LegacyCommandExecutionErrorHandler {

    @Override
    public void onArgumentsParseError(
            @NotNull BukkitCommandDefinition command,
            @NotNull CommandSender sender,
            @NotNull BukkitArgumentsParseException exception
    ) {
        sender.sendMessage(
                ChatColor.RED +
                        switch (exception.getType()) {
                            case INVALID_ARGUMENT ->
                                    formatInvalidArgument(sender, command, exception);
                            case MISSING_ARGUMENT ->
                                    formatMissingArgument(sender, command, exception);
                            case UNKNOWN_ARGUMENT ->
                                    formatUnknownArgument(exception);
                        }
        );
    }

    private @NotNull String formatInvalidArgument(
            @NotNull CommandSender sender,
            @NotNull BukkitCommandDefinition command,
            @NotNull BukkitArgumentsParseException exception
    ) {
        String input = exception.getInput();
        String prefix = "Input: ";

        return "\n"
                + "Invalid argument: " + exception.getMessage()
                + "\n"
                + "Usage: " + command.buildUsageString(sender)
                + "\n"
                + caret(
                prefix + input,
                prefix.length() + exception.getStart(),
                prefix.length() + exception.getEnd()
        )
                + "\n";
    }

    private @NotNull String formatMissingArgument(
            @NotNull CommandSender sender,
            @NotNull BukkitCommandDefinition command,
            @NotNull BukkitArgumentsParseException exception
    ) {
        String usage = command.buildUsageString(sender);
        String argument = "<" + exception.getArgument().getName() + ">";

        int start = usage.indexOf(argument);

        return "\n"
                + exception.getMessage()
                + "\n"
                + caret(
                usage,
                start,
                start + argument.length()
        )
                + "\n";
    }

    private @NotNull String formatUnknownArgument(
            @NotNull BukkitArgumentsParseException exception
    ) {
        return "\n"
                + exception.getMessage()
                + "\n"
                + caret(
                exception.getInput(),
                exception.getStart(),
                exception.getEnd()
        )
                + "\n";
    }

    private @NotNull String caret(
            @NotNull String input,
            int start,
            int end
    ) {
        return input
                + "\n"
                + " ".repeat(start)
                + "^".repeat(Math.max(1, end - start));
    }
}