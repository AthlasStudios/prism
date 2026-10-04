package dev.gurwi.prism.bukkit.argument.parser;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.GreedyArgument;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import dev.gurwi.prism.bukkit.exception.GreedyArgumentException;
import dev.gurwi.prism.bukkit.exception.UnavailableArgumentException;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LegacyArgumentsParser {

    private LegacyArgumentsParser() {
    }

    public static @NotNull CommandArguments<BukkitCommandDefinition, CommandSender> parse(@NotNull CommandSender sender, @NotNull BukkitCommandDefinition definition, @NotNull String @NotNull [] rawArgs) throws BukkitArgumentsParseException {
        return parse(sender, definition, rawArgs, rawArgs, 0, null);
    }

    public static @NotNull CommandArguments<BukkitCommandDefinition, CommandSender> parse(@NotNull CommandSender sender, @NotNull BukkitCommandDefinition definition, @NotNull String @NotNull [] rawArgs, @NotNull String @NotNull [] originalArgs, int offset, @Nullable CommandArguments<BukkitCommandDefinition, CommandSender> parent) throws BukkitArgumentsParseException {
        validateArguments(definition);

        Map<String, Object> required = new HashMap<>();
        Map<String, Object> optional = new HashMap<>();

        int position = parseInto(required, sender, definition.getRequiredArguments(), rawArgs, 0, originalArgs, offset, true);
        position = parseInto(optional, sender, definition.getOptionalArguments(), rawArgs, position, originalArgs, offset, false);

        if (position < rawArgs.length) {
            int start = inputStart(originalArgs, offset + position);

            throw BukkitArgumentsParseException.unknown(String.join(" ", originalArgs), start, start + rawArgs[position].length());
        }

        return new CommandArguments<>(definition, required, optional, parent);
    }

    private static int parseInto(@NotNull Map<String, Object> target, @NotNull CommandSender sender, @NotNull List<BukkitArgument<?, ?>> arguments, @NotNull String @NotNull [] rawArgs, int start, @NotNull String @NotNull [] originalArgs, int offset, boolean required) throws BukkitArgumentsParseException {
        int position = start;

        for (BukkitArgument<?, ?> argument : arguments) {
            if (!argument.getRequirement().test(sender)) {
                throw new UnavailableArgumentException();
            }

            if (position >= rawArgs.length) {
                if (required) {
                    throw BukkitArgumentsParseException.missing(argument, 0, 0);
                }

                break;
            }

            int end = ArgumentTokens.advance(argument, position, rawArgs.length);

            if (end > rawArgs.length) {
                throw BukkitArgumentsParseException.missing(argument, 0, 0);
            }

            String input = String.join(" ", Arrays.copyOfRange(rawArgs, position, end));

            try {
                target.put(argument.getName(), argument.parse(sender, input));
            } catch (ArgumentParseException e) {
                int from = inputStart(originalArgs, offset + position);

                throw BukkitArgumentsParseException.invalid(e, String.join(" ", originalArgs), from, from + input.length());
            }

            position = end;
        }

        return position;
    }

    private static void validateArguments(@NotNull BukkitCommandDefinition definition) {
        List<BukkitArgument<?, ?>> arguments = new ArrayList<>(definition.getRequiredArguments());
        arguments.addAll(definition.getOptionalArguments());

        for (int i = 0; i < arguments.size() - 1; i++) {
            if (arguments.get(i) instanceof GreedyArgument) {
                throw new GreedyArgumentException("A greedy argument must be the last argument.");
            }
        }
    }

    private static int inputStart(@NotNull String @NotNull [] rawArgs, int index) {
        int start = 0;

        for (int i = 0; i < index; i++) {
            start += rawArgs[i].length() + 1;
        }

        return start;
    }

}