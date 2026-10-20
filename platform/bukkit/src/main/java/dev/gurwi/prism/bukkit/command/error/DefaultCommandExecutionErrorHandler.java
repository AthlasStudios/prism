package dev.gurwi.prism.bukkit.command.error;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import dev.gurwi.prism.reflection.ReflectionUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultCommandExecutionErrorHandler implements LegacyCommandExecutionErrorHandler {

    private static final ChatColor ERROR = parseColor("#FF5555", ChatColor.RED);
    private static final ChatColor TEXT = ChatColor.GRAY;
    private static final int CARET_WIDTH = 6;

    private final LegacyCommandExecutionErrorHandler consoleHandler = LegacyCommandExecutionErrorHandler.consoleHandler();

    protected DefaultCommandExecutionErrorHandler() {
    }

    @Override
    public void onArgumentsParseError(
            @NotNull BukkitCommandDefinition command,
            @NotNull CommandSender sender,
            @NotNull BukkitArgumentsParseException exception
    ) {
        if (sender instanceof ConsoleCommandSender) {
            consoleHandler.onArgumentsParseError(command, sender, exception);
            return;
        }

        sender.sendMessage("");

        switch (exception.getType()) {
            case INVALID_ARGUMENT:
                sendError(sender, "Invalid argument", command, exception, false);
                break;
            case MISSING_ARGUMENT:
                sendError(sender, "Missing argument", command, exception, true);
                break;
            case UNKNOWN_ARGUMENT:
                sendError(sender, "Unknown argument", command, exception, false);
                break;
            default:
                throw new IllegalStateException("Unexpected type: " + exception.getType());
        }

        sender.sendMessage("");
    }

    private void sendError(
            @NotNull CommandSender sender,
            @NotNull String title,
            @NotNull BukkitCommandDefinition command,
            @NotNull BukkitArgumentsParseException exception,
            boolean missing
    ) {
        String errorMessage = exception.getMessage();
        String displayTitle = (errorMessage != null && !errorMessage.isEmpty())
                ? title + ": " + errorMessage
                : title;

        send(sender, text(displayTitle, ERROR));

        List<String> tokens = usageTokens(command.buildUsageString(sender));
        String rawInput = exception.getInput() == null ? "" : exception.getInput();
        String[] inputTokens = rawInput.trim().isEmpty() ? new String[0] : rawInput.trim().split("\\s+");

        int literalCount = literalCount(tokens);
        int overlap = overlap(tokens, literalCount, inputTokens);
        int headCount = literalCount - overlap;

        String head = "/" + (headCount > 0 ? String.join(" ", tokens.subList(0, headCount)) + " " : "");

        String fullCommand;
        int start;
        int end;

        if (missing) {
            String placeholder;
            if (exception.getArgument() != null) {
                placeholder = "<" + exception.getArgument().getName() + ">";
            } else {
                int index = headCount + inputTokens.length;
                placeholder = index < tokens.size() ? tokens.get(index) : "<argument>";
            }

            String typed = head + rawInput.trim();
            if (!typed.endsWith(" ")) {
                typed = typed + " ";
            }

            fullCommand = typed + placeholder;
            start = typed.length();
            end = fullCommand.length();
        } else {
            fullCommand = head + rawInput;
            start = clamp(exception.getStart() + head.length(), 0, fullCommand.length());
            end = clamp(exception.getEnd() + head.length(), start, fullCommand.length());
        }

        String before = fullCommand.substring(0, start);
        String wrongPart = fullCommand.substring(start, end);
        String after = fullCommand.substring(end);

        TextComponent commandLine = new TextComponent("");
        if (!before.isEmpty()) {
            commandLine.addExtra(text(before, TEXT));
        }
        if (!wrongPart.isEmpty()) {
            commandLine.addExtra(text(wrongPart, ERROR));
        }
        if (!after.isEmpty()) {
            commandLine.addExtra(text(after, TEXT));
        }

        send(sender, commandLine);

        int caretCount = wrongPart.isEmpty()
                ? 1
                : Math.max(1, Math.round(pixelWidth(wrongPart) / (float) CARET_WIDTH));

        TextComponent caretLine = new TextComponent("");
        caretLine.addExtra(padding(pixelWidth(before)));
        caretLine.addExtra(text("^".repeat(caretCount), ERROR));

        send(sender, caretLine);
    }

    private @NotNull List<String> usageTokens(@NotNull String usage) {
        String cleaned = usage.startsWith("/") ? usage.substring(1) : usage;
        cleaned = cleaned.trim();
        if (cleaned.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(cleaned.split("\\s+")));
    }

    private int literalCount(@NotNull List<String> tokens) {
        int count = 0;
        for (String token : tokens) {
            if (token.startsWith("<") || token.startsWith("[") || token.startsWith("(")) {
                break;
            }
            count++;
        }
        return count;
    }

    private int overlap(@NotNull List<String> tokens, int literalCount, @NotNull String[] inputTokens) {
        for (int k = Math.min(literalCount - 1, inputTokens.length); k >= 1; k--) {
            boolean match = true;
            for (int i = 0; i < k; i++) {
                if (!tokens.get(literalCount - k + i).equals(inputTokens[i])) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return k;
            }
        }
        return 0;
    }

    private @NotNull TextComponent padding(int pixels) {
        int bold = 0;
        int normal = 0;
        int bestError = Integer.MAX_VALUE;

        for (int b = 0; b * 5 <= pixels; b++) {
            int remaining = pixels - b * 5;
            int a = remaining / 4;
            int error = remaining - a * 4;
            if (error < bestError || (error == bestError && b < bold)) {
                bestError = error;
                bold = b;
                normal = a;
            }
            if (error == 0) {
                break;
            }
        }

        TextComponent result = new TextComponent("");
        if (normal > 0) {
            result.addExtra(new TextComponent(" ".repeat(normal)));
        }
        if (bold > 0) {
            TextComponent boldSpaces = new TextComponent(" ".repeat(bold));
            boldSpaces.setBold(true);
            result.addExtra(boldSpaces);
        }
        return result;
    }

    private int pixelWidth(@NotNull String value) {
        int total = 0;
        for (int i = 0; i < value.length(); i++) {
            total += charWidth(value.charAt(i));
        }
        return total;
    }

    private int charWidth(char c) {
        switch (c) {
            case ' ':
            case 'I':
            case 't':
            case '[':
            case ']':
                return 4;
            case 'i':
            case '!':
            case '.':
            case ',':
            case ':':
            case ';':
            case '|':
            case '\'':
                return 2;
            case 'l':
            case '`':
                return 3;
            case 'f':
            case 'k':
            case '(':
            case ')':
            case '{':
            case '}':
            case '<':
            case '>':
            case '*':
            case '"':
                return 5;
            case '@':
            case '~':
                return 7;
            default:
                return 6;
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private @NotNull BaseComponent line(@NotNull BaseComponent @NotNull ... parts) {
        TextComponent parent = new TextComponent("");
        for (BaseComponent part : parts) {
            parent.addExtra(part);
        }
        return parent;
    }

    private @NotNull TextComponent text(@NotNull String value, @Nullable ChatColor color) {
        TextComponent component = new TextComponent(value);
        if (color != null) {
            component.setColor(color);
        }
        return component;
    }

    private void send(@NotNull CommandSender sender, @NotNull BaseComponent component) {
        sender.spigot().sendMessage(component);
    }

    private static @NotNull ChatColor parseColor(@NotNull String hex, @NotNull ChatColor fallback) {
        try {
            return ReflectionUtil.callMethod(ChatColor.class, "of", hex);
        } catch (Throwable ignored) {
            return fallback;
        }
    }
}