package dev.gurwi.prism.bukkit.argument.parser;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.base.GreedyArgument;
import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import dev.gurwi.prism.bukkit.exception.GreedyArgumentException;
import dev.gurwi.prism.bukkit.exception.UnavailableArgumentException;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class LegacyArgumentsParser {

    private LegacyArgumentsParser() {
    }

    public record TreeParseResult(@NotNull CommandArguments<BukkitCommandDefinition, CommandSender> arguments, @Nullable BukkitArgument<?, ?> node) {
    }

    private record Attempt(@Nullable BukkitArgument<?, ?> node, @NotNull Map<String, Object> values, @Nullable Exception failure, int depth) {

        @Contract("_, _ -> new")
        static @NotNull Attempt ok(@NotNull BukkitArgument<?, ?> node, @NotNull Map<String, Object> values) {
            return new Attempt(node, values, null, 0);
        }

        @Contract("_, _ -> new")
        static @NotNull Attempt fail(@NotNull Exception failure, int depth) {
            return new Attempt(null, Map.of(), failure, depth);
        }

        boolean succeeded() {
            return failure == null;
        }

    }

    public static @NotNull CommandArguments<BukkitCommandDefinition, CommandSender> parse(@NotNull CommandSender sender, @NotNull BukkitCommandDefinition definition, @NotNull String @NotNull [] rawArgs) throws BukkitArgumentsParseException {
        return parse(sender, definition, rawArgs, rawArgs, 0, null);
    }

    public static @NotNull CommandArguments<BukkitCommandDefinition, CommandSender> parse(@NotNull CommandSender sender, @NotNull BukkitCommandDefinition definition, @NotNull String @NotNull [] rawArgs, @NotNull String @NotNull [] originalArgs, int offset, @Nullable CommandArguments<BukkitCommandDefinition, CommandSender> parent) throws BukkitArgumentsParseException {
        if (!definition.getArgumentTreeBranches().isEmpty()) {
            throw new IllegalStateException("Definition '" + definition.getName() + "' has argument tree branches, use parseTree");
        }

        return parseTree(sender, definition, rawArgs, originalArgs, offset, parent).arguments();
    }

    public static @NotNull TreeParseResult parseTree(@NotNull CommandSender sender, @NotNull BukkitCommandDefinition definition, @NotNull String @NotNull [] rawArgs, @NotNull String @NotNull [] originalArgs, int offset, @Nullable CommandArguments<BukkitCommandDefinition, CommandSender> parent) throws BukkitArgumentsParseException {
        validateArguments(definition);

        Map<String, Object> required = new HashMap<>();
        Map<String, Object> optional = new HashMap<>();

        int position = parseInto(required, sender, definition.getRequiredArguments(), rawArgs, 0, originalArgs, offset, true);
        position = parseInto(optional, sender, definition.getOptionalArguments(), rawArgs, position, originalArgs, offset, false);

        if (position >= rawArgs.length) {
            return new TreeParseResult(new CommandArguments<>(definition, required, optional, parent), null);
        }

        List<BukkitArgument<?, ?>> branches = nodes(definition.getArgumentTreeBranches());

        if (branches.isEmpty()) {
            throw unknownAt(originalArgs, offset, rawArgs, position);
        }

        Attempt attempt = attempt(sender, branches, rawArgs, position, originalArgs, offset);

        if (!attempt.succeeded()) {
            throw rethrow(Objects.requireNonNull(attempt.failure()));
        }

        required.putAll(attempt.values());

        return new TreeParseResult(new CommandArguments<>(definition, required, optional, parent), attempt.node());
    }

    private static @NotNull Attempt attempt(@NotNull CommandSender sender, @NotNull List<BukkitArgument<?, ?>> candidates, @NotNull String @NotNull [] rawArgs, int position, @NotNull String @NotNull [] originalArgs, int offset) throws BukkitArgumentsParseException {
        List<BukkitArgument<?, ?>> ordered = new ArrayList<>();

        for (BukkitArgument<?, ?> candidate : candidates) {
            if (candidate instanceof LiteralArgument) {
                ordered.add(candidate);
            }
        }

        for (BukkitArgument<?, ?> candidate : candidates) {
            if (!(candidate instanceof LiteralArgument)) {
                ordered.add(candidate);
            }
        }

        Attempt deepest = null;

        for (BukkitArgument<?, ?> candidate : ordered) {
            Attempt result = attemptNode(sender, candidate, rawArgs, position, originalArgs, offset);

            if (result.succeeded()) {
                return result;
            }

            if (deepest == null || result.depth() > deepest.depth()) {
                deepest = result;
            }
        }

        return Objects.requireNonNull(deepest);
    }

    private static @NotNull Attempt attemptNode(@NotNull CommandSender sender, @NotNull BukkitArgument<?, ?> node, @NotNull String @NotNull [] rawArgs, int position, @NotNull String @NotNull [] originalArgs, int offset) throws BukkitArgumentsParseException {
        List<BukkitArgument<?, ?>> children = nodes(node.getArguments());

        if (node instanceof GreedyArgument && !children.isEmpty()) {
            throw new GreedyArgumentException("A greedy argument cannot have children.");
        }

        if (!node.canUse(sender)) {
            return Attempt.fail(new UnavailableArgumentException(), position);
        }

        boolean literal = node instanceof LiteralArgument;
        Object value = null;
        int end;

        if (literal) {
            if (!rawArgs[position].equals(node.getName())) {
                return Attempt.fail(unknownAt(originalArgs, offset, rawArgs, position), position);
            }

            end = position + 1;
        } else {
            end = ArgumentTokens.advance(node, position, rawArgs.length);

            if (end > rawArgs.length) {
                return Attempt.fail(missingAt(node, originalArgs), rawArgs.length);
            }

            String input = String.join(" ", Arrays.copyOfRange(rawArgs, position, end));

            try {
                value = node.parse(sender, input);
            } catch (ArgumentParseException e) {
                int from = inputStart(originalArgs, offset + position);

                return Attempt.fail(BukkitArgumentsParseException.invalid(e, String.join(" ", originalArgs), from, from + input.length()), position);
            }
        }

        Map<String, Object> values = new HashMap<>();

        if (!literal && node.isListed()) {
            values.put(node.getName(), value);
        }

        if (end >= rawArgs.length) {
            return Attempt.ok(node, values);
        }

        if (children.isEmpty()) {
            return Attempt.fail(unknownAt(originalArgs, offset, rawArgs, end), end);
        }

        Attempt child = attempt(sender, children, rawArgs, end, originalArgs, offset);

        if (!child.succeeded()) {
            return child;
        }

        Map<String, Object> merged = new HashMap<>(values);
        merged.putAll(child.values());

        return Attempt.ok(Objects.requireNonNull(child.node()), merged);
    }

    private static int parseInto(@NotNull Map<String, Object> target, @NotNull CommandSender sender, @NotNull List<BukkitArgument<?, ?>> arguments, @NotNull String @NotNull [] rawArgs, int start, @NotNull String @NotNull [] originalArgs, int offset, boolean required) throws BukkitArgumentsParseException {
        int position = start;

        for (BukkitArgument<?, ?> argument : arguments) {
            if (!argument.canUse(sender)) {
                throw new UnavailableArgumentException();
            }

            if (position >= rawArgs.length) {
                if (required) {
                    throw missingAt(argument, originalArgs);
                }

                break;
            }

            if (argument instanceof LiteralArgument) {
                if (!rawArgs[position].equals(argument.getName())) {
                    if (required) {
                        throw unknownAt(originalArgs, offset, rawArgs, position);
                    }

                    break;
                }

                position++;
                continue;
            }

            int end = ArgumentTokens.advance(argument, position, rawArgs.length);

            if (end > rawArgs.length) {
                throw missingAt(argument, originalArgs);
            }

            String input = String.join(" ", Arrays.copyOfRange(rawArgs, position, end));

            try {
                Object value = argument.parse(sender, input);

                if (argument.isListed()) {
                    target.put(argument.getName(), value);
                }
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

    private static @NotNull BukkitArgumentsParseException missingAt(@NotNull BukkitArgument<?, ?> argument, @NotNull String @NotNull [] originalArgs) {
        String input = String.join(" ", originalArgs);

        return BukkitArgumentsParseException.missing(argument, input, input.length(), input.length());
    }

    private static @NotNull BukkitArgumentsParseException unknownAt(@NotNull String @NotNull [] originalArgs, int offset, @NotNull String @NotNull [] rawArgs, int position) {
        int start = inputStart(originalArgs, offset + position);

        return BukkitArgumentsParseException.unknown(String.join(" ", originalArgs), start, start + rawArgs[position].length());
    }

    private static @NotNull BukkitArgumentsParseException rethrow(@NotNull Exception failure) {
        if (failure instanceof BukkitArgumentsParseException parse) {
            return parse;
        }

        if (failure instanceof RuntimeException runtime) {
            throw runtime;
        }

        throw new IllegalStateException(failure);
    }

    private static @NotNull List<BukkitArgument<?, ?>> nodes(@NotNull Collection<?> source) {
        List<BukkitArgument<?, ?>> result = new ArrayList<>();

        for (Object item : source) {
            result.add((BukkitArgument<?, ?>) item);
        }

        return result;
    }

    private static int inputStart(@NotNull String @NotNull [] rawArgs, int index) {
        int start = 0;

        for (int i = 0; i < index; i++) {
            start += rawArgs[i].length() + 1;
        }

        return start;
    }

}