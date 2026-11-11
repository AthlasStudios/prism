package dev.gurwi.prism.bukkit.command.error;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentParseException;
import dev.gurwi.prism.exception.CommandExecutionException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommandExecutionErrorHandler implements LegacyCommandExecutionErrorHandler {

    @Override
    public void onArgumentsParseError(@NotNull BukkitCommandDefinition command, @NotNull CommandSender sender, @NotNull BukkitArgumentParseException exception) {
        if (exception instanceof BukkitArgumentParseException.Silent) {
            exception.printStackTrace(); // TODO -> ADD MORE ROBUST LOGGING
            return;
        }

        if (exception instanceof BukkitArgumentParseException.Message) {
            sender.sendMessage(exception.getMessage());
            exception.printStackTrace(); // TODO -> ADD MORE ROBUST LOGGING
            return;
        }

        if (exception instanceof BukkitArgumentParseException.Panic) {
            throw exception;
        }

        sender.sendMessage(ChatColor.RED + format(sender, command, exception));
    }

    @Override
    public void onCommandExecutionError(@NotNull BukkitCommandDefinition command, @NotNull CommandSender sender, @NotNull CommandExecutionException exception) {
        if (exception instanceof CommandExecutionException.Message) {
            sender.sendMessage(exception.getMessage());
            exception.printStackTrace();

            return;
        }

        if (exception instanceof CommandExecutionException.Silent) {
            exception.printStackTrace();
            return;
        }

        throw exception;
    }

    private @NotNull String format(@NotNull CommandSender sender, @NotNull BukkitCommandDefinition command, @NotNull BukkitArgumentParseException exception) {
        if (exception instanceof BukkitArgumentParseException.Invalid) {
            return formatInvalidArgument(sender, command, exception);
        } else if (exception instanceof BukkitArgumentParseException.Missing) {
            return formatMissingArgument(sender, command, exception);
        } else if (exception instanceof BukkitArgumentParseException.Unknown) {
            return formatUnknownArgument(exception);
        } else {
            throw new IllegalStateException("Unexpected type: " + exception.getClass().getSimpleName());
        }
    }

    private @NotNull String formatInvalidArgument(@NotNull CommandSender sender, @NotNull BukkitCommandDefinition command, @NotNull BukkitArgumentParseException exception) {
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

    private @NotNull String formatMissingArgument(@NotNull CommandSender sender, @NotNull BukkitCommandDefinition command, @NotNull BukkitArgumentParseException exception) {
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

    private @NotNull String formatUnknownArgument(@NotNull BukkitArgumentParseException exception) {
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

    private @NotNull String caret(@NotNull String input, int start, int end) {
        return input
                + "\n"
                + " ".repeat(start)
                + "^".repeat(Math.max(1, end - start));
    }

}