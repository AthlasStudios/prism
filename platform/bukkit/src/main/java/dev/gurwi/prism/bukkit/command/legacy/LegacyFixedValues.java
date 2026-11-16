package dev.gurwi.prism.bukkit.command.legacy;

import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.argument.StructureRotationArgument;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.base.LiteralValues;
import dev.gurwi.prism.bukkit.argument.parser.ArgumentTokens;
import dev.gurwi.prism.bukkit.argument.parser.LegacyArgumentsParser;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

final class LegacyFixedValues {

    private LegacyFixedValues() {
    }

    static @Nullable BukkitArgumentParseException check(@NotNull CommandSender sender, @NotNull BukkitCommandDefinition definition, @NotNull String @NotNull [] tokens, int start, @NotNull String @NotNull [] all) {
        List<BukkitArgument<?, ?>> arguments = new ArrayList<>(definition.getRequiredArguments());
        arguments.addAll(definition.getOptionalArguments());

        int position = 0;

        for (BukkitArgument<?, ?> argument : arguments) {
            if (position >= tokens.length) {
                return null;
            }

            if (argument instanceof LiteralArgument) {
                if (!tokens[position].equals(argument.getName())) {
                    return null;
                }

                position++;
                continue;
            }

            int end = Math.min(ArgumentTokens.advance(argument, position, tokens.length), tokens.length);
            String input = String.join(" ", Arrays.copyOfRange(tokens, position, end));

            if (!allows(argument, input)) {
                return invalid(new Failure(argument, input, start + position), all);
            }

            position = end;
        }

        if (position >= tokens.length) {
            return null;
        }

        Failure failure = checkBranches(sender, LegacyArgumentsParser.nodes(definition.getArgumentTreeBranches()), tokens, position, start);

        return failure == null ? null : invalid(failure, all);
    }

    static boolean allows(@NotNull BukkitArgument<?, ?> argument, @NotNull String input) {
        Optional<LiteralValues> fixed = argument.getFixedValues();

        if (fixed.isEmpty()) {
            return true;
        }

        if (argument instanceof StructureRotationArgument && input.equals("180")) {
            return true;
        }

        String stripped = input.regionMatches(true, 0, "minecraft:", 0, 10) ? input.substring(10) : input;

        return fixed.get().get().stream().anyMatch(value -> value.equalsIgnoreCase(input) || value.equalsIgnoreCase(stripped));
    }

    private static @Nullable Failure checkBranches(@NotNull CommandSender sender, @NotNull List<BukkitArgument<?, ?>> candidates, @NotNull String @NotNull [] tokens, int position, int base) {
        if (position >= tokens.length) {
            return null;
        }

        Failure deepest = null;

        for (BukkitArgument<?, ?> candidate : candidates) {
            if (!candidate.canUse(sender)) {
                continue;
            }

            int end;

            if (candidate instanceof LiteralArgument) {
                if (!tokens[position].equals(candidate.getName())) {
                    continue;
                }

                end = position + 1;
            } else {
                end = Math.min(ArgumentTokens.advance(candidate, position, tokens.length), tokens.length);
                String input = String.join(" ", Arrays.copyOfRange(tokens, position, end));

                if (!allows(candidate, input)) {
                    Failure failure = new Failure(candidate, input, base + position);

                    if (deepest == null || failure.position > deepest.position) {
                        deepest = failure;
                    }

                    continue;
                }
            }

            Failure child = checkBranches(sender, LegacyArgumentsParser.nodes(candidate.getArguments()), tokens, end, base);

            if (child == null) {
                return null;
            }

            if (deepest == null || child.position >= deepest.position) {
                deepest = child;
            }
        }

        return deepest;
    }

    private static @NotNull BukkitArgumentParseException invalid(@NotNull Failure failure, @NotNull String @NotNull [] all) {
        List<String> values = failure.argument.getFixedValues().map(LiteralValues::get).orElse(List.of());
        String expected = values.size() <= 10
                ? String.join(", ", values)
                : String.join(", ", values.subList(0, 10)) + ", ... (" + (values.size() - 10) + " more)";

        int from = 0;

        for (int i = 0; i < failure.position; i++) {
            from += all[i].length() + 1;
        }

        return BukkitArgumentParseException.invalid(failure.argument, String.join(" ", all), from, from + failure.input.length(), "Expected one of " + expected + " got " + failure.input);
    }

    private static final class Failure {

        private final BukkitArgument<?, ?> argument;
        private final String input;
        private final int position;

        private Failure(@NotNull BukkitArgument<?, ?> argument, @NotNull String input, int position) {
            this.argument = argument;
            this.input = input;
            this.position = position;
        }

    }

}