package dev.gurwi.prism.bukkit.argument.parser;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.GreedyArgument;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import dev.gurwi.prism.bukkit.exception.GreedyArgumentException;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LegacyArgumentsParser {

    private LegacyArgumentsParser() {
    }

    public static @NotNull CommandArguments parse(@NotNull BukkitCommandDefinition definition, @NotNull String[] rawArgs) throws BukkitArgumentsParseException {
        validateArguments(definition);

        Map<String, Object> required = parseArguments(
                definition,
                definition.getRequiredArguments(),
                rawArgs,
                true
        );

        int consumed = consumedArguments(definition.getRequiredArguments(), rawArgs);

        String[] remainingArgs = Arrays.copyOfRange(
                rawArgs,
                consumed,
                rawArgs.length
        );

        Map<String, Object> optional = parseArguments(
                definition,
                definition.getOptionalArguments(),
                remainingArgs,
                false
        );

        consumed += consumedArguments(
                definition.getOptionalArguments(),
                remainingArgs
        );

        if (consumed < rawArgs.length) {
            String input = String.join(" ", rawArgs);
            int start = inputStart(rawArgs, consumed);

            throw BukkitArgumentsParseException.unknown(
                    input,
                    start,
                    start + rawArgs[consumed].length()
            );
        }

        return new CommandArguments(required, optional);
    }

    private static @NotNull Map<String, Object> parseArguments(
            @NotNull BukkitCommandDefinition definition,
            @NotNull List<BukkitArgument<?, ?>> arguments,
            @NotNull String[] rawArgs,
            boolean required
    ) throws BukkitArgumentsParseException {

        Map<String, Object> parsed = new HashMap<>();

        for (int i = 0; i < arguments.size(); i++) {
            BukkitArgument<?, ?> argument = arguments.get(i);

            if (i >= rawArgs.length) {
                if (required) {
                    String usage = definition.getUsageString();
                    int start = usageArgumentStart(
                            usage,
                            argument
                    );

                    throw BukkitArgumentsParseException.missing(
                            argument,
                            usage,
                            start,
                            start + argumentToken(argument).length()
                    );
                }

                break;
            }

            if (argument instanceof GreedyArgument) {
                String input = String.join(
                        " ",
                        Arrays.copyOfRange(
                                rawArgs,
                                i,
                                rawArgs.length
                        )
                );

                try {
                    parsed.put(
                            argument.getName(),
                            argument.parse(input)
                    );
                } catch (ArgumentParseException e) {
                    int start = inputStart(rawArgs, i);

                    throw BukkitArgumentsParseException.invalid(
                            e,
                            String.join(" ", rawArgs),
                            start,
                            start + input.length()
                    );
                }

                break;
            }

            try {
                parsed.put(
                        argument.getName(),
                        argument.parse(rawArgs[i])
                );
            } catch (ArgumentParseException e) {
                int start = inputStart(rawArgs, i);

                throw BukkitArgumentsParseException.invalid(
                        e,
                        String.join(" ", rawArgs),
                        start,
                        start + rawArgs[i].length()
                );
            }
        }

        return parsed;
    }

    private static void validateArguments(@NotNull BukkitCommandDefinition definition) {
        List<BukkitArgument<?, ?>> arguments = new ArrayList<>();

        arguments.addAll(definition.getRequiredArguments());
        arguments.addAll(definition.getOptionalArguments());

        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i) instanceof GreedyArgument
                    && i != arguments.size() - 1) {
                throw new GreedyArgumentException(
                        "A greedy argument must be the last argument."
                );
            }
        }
    }

    private static int consumedArguments(@NotNull List<BukkitArgument<?, ?>> arguments, @NotNull String[] rawArgs) {
        int consumed = 0;

        for (int i = 0; i < arguments.size() && i < rawArgs.length; i++) {
            consumed++;

            if (arguments.get(i) instanceof GreedyArgument) {
                return rawArgs.length;
            }
        }

        return consumed;
    }

    private static int inputStart(@NotNull String[] rawArgs, int index) {
        int start = 0;

        for (int i = 0; i < index; i++) {
            start += rawArgs[i].length() + 1;
        }

        return start;
    }

    private static int usageArgumentStart(@NotNull String usage, @NotNull BukkitArgument<?, ?> argument) {
        return usage.indexOf(argumentToken(argument));
    }

    private static @NotNull String argumentToken(@NotNull BukkitArgument<?, ?> argument) {
        return "<" + argument.getName() + ">";
    }
}