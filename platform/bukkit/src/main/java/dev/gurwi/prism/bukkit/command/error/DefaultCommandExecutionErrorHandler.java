 package dev.gurwi.prism.bukkit.command.error;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultCommandExecutionErrorHandler implements LegacyCommandExecutionErrorHandler {

    protected DefaultCommandExecutionErrorHandler() {

    }

    @Override
    public void onArgumentsParseError(
            @NotNull BukkitCommandDefinition command,
            @NotNull CommandSender sender,
            @NotNull BukkitArgumentsParseException exception
    ) {
        sender.sendMessage(
                ChatColor.RED +
                        switch (exception.getType()) {
                            case INVALID_ARGUMENT -> formatInvalidArgument(sender, command, exception);
                            case MISSING_ARGUMENT -> formatMissingArgument(sender, exception);
                            case UNKNOWN_ARGUMENT -> formatUnknownArgument(sender, exception);
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
                + "Usage: " + command.getUsageString()
                + "\n"
                + formatInput(sender, prefix + input,
                prefix.length() + exception.getStart(),
                prefix.length() + exception.getEnd())
                + "\n";
    }

    private @NotNull String formatMissingArgument(
            @NotNull CommandSender sender,
            @NotNull BukkitArgumentsParseException exception
    ) {
        return "\n"
                + exception.getMessage()
                + "\n"
                + formatInput(
                sender,
                exception.getInput(),
                exception.getStart(),
                exception.getEnd()
        )
                + "\n";
    }

    private @NotNull String formatUnknownArgument(
            @NotNull CommandSender sender,
            @NotNull BukkitArgumentsParseException exception
    ) {
        return "\n"
                + exception.getMessage()
                + "\n"
                + formatInput(
                sender,
                exception.getInput(),
                exception.getStart(),
                exception.getEnd()
        )
                + "\n";
    }

    private @NotNull String formatInput(
            @NotNull CommandSender sender,
            @NotNull String input,
            int start,
            int end
    ) {
        if (sender instanceof Player) {
            return underline(input, start, end);
        }

        return input
                + "\n"
                + " ".repeat(start)
                + "^".repeat(Math.max(1, end - start));
    }

    private @NotNull String underline(
            @NotNull String input,
            int start,
            int end
    ) {
        return input.substring(0, start)
                + ChatColor.DARK_RED
                + ChatColor.UNDERLINE
                + input.substring(start, end)
                + ChatColor.RED
                + input.substring(end);
    }
}