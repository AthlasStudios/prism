package dev.gurwi.prism.bukkit.command.legacy;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.argument.ParsedArgument;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.base.LiteralValues;
import dev.gurwi.prism.bukkit.argument.parser.ArgumentTokens;
import dev.gurwi.prism.bukkit.argument.parser.LegacyArgumentsParser;
import dev.gurwi.prism.bukkit.argument.suggestion.ArgumentSuggestion;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestionsSupplier;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.event.AsyncCommandSuggestEvent;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class LegacyTabCompleter {

    private static final ThreadLocal<List<CompletableFuture<List<String>>>> ASYNC = new ThreadLocal<>();

    private LegacyTabCompleter() {
    }

    static @NotNull List<String> complete(@NotNull BukkitCommandDefinition root, @NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        return complete(root, sender, args, 0, null, input(alias, args));
    }

    static @NotNull Optional<CompletableFuture<List<String>>> completeAsync(@NotNull BukkitCommandDefinition root, @NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        List<String> immediate;

        ASYNC.set(futures);

        try {
            immediate = complete(root, sender, root.getName(), args);
        } finally {
            ASYNC.remove();
        }

        if (futures.isEmpty()) return Optional.empty();

        return Optional.of(CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenApply(ignored -> {
            Set<String> merged = new LinkedHashSet<>(immediate);

            for (CompletableFuture<List<String>> future : futures) {
                merged.addAll(future.join());
            }

            return List.copyOf(merged);
        }));
    }

    static @NotNull Optional<BukkitCommandDefinition> findChild(@NotNull BukkitCommandDefinition current, @NotNull CommandSender sender, @NotNull String token) {
        return current.getChild(token.toLowerCase())
                .filter(candidate -> candidate.canExecute(sender));
    }

    private static @NotNull List<String> complete(@NotNull BukkitCommandDefinition current, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int offset, @Nullable CommandArguments<BukkitCommandDefinition, CommandSender> parent, @NotNull String fullInput) {
        List<BukkitArgument<?, ?>> required = current.getRequiredArguments();
        List<BukkitArgument<?, ?>> optional = current.getOptionalArguments();
        List<BukkitArgument<?, ?>> branches = LegacyArgumentsParser.nodes(current.getArgumentTreeBranches());

        int last = args.length - 1;
        String input = args[last];

        Located located = locate(required, last - offset);

        if (located != null) {
            int start = offset + located.start;

            return suggestFrom(located.argument, sender, args, start, last, new Trail(current, sender, args, offset, start, parent, fullInput).arguments());
        }

        int position = offset;

        for (BukkitArgument<?, ?> argument : required) {
            position = ArgumentTokens.advance(argument, position, Integer.MAX_VALUE);
        }

        int optionalIndex = 0;

        while (position < last) {
            Optional<BukkitCommandDefinition> child = findChild(current, sender, args[position]);
            Trail trail = new Trail(current, sender, args, offset, position, parent, fullInput);

            if (child.isPresent()) {
                return complete(child.get(), sender, args, position + 1, trail.arguments().get(), fullInput);
            }

            if (optionalIndex >= optional.size()) {
                return completeBranches(branches, sender, args, position, last, trail);
            }

            BukkitArgument<?, ?> next = optional.get(optionalIndex);
            int end = ArgumentTokens.advance(next, position, Integer.MAX_VALUE);

            if (last < end) {
                return suggestFrom(next, sender, args, position, last, trail.arguments());
            }

            position = end;
            optionalIndex++;
        }

        Set<String> suggestions = new LinkedHashSet<>();
        Trail trail = new Trail(current, sender, args, offset, position, parent, fullInput);

        if (optionalIndex < optional.size()) {
            suggestions.addAll(suggest(optional.get(optionalIndex), sender, input, "", trail.arguments()));
        } else {
            suggestions.addAll(completeBranches(branches, sender, args, last, last, trail));
        }

        suggestions.addAll(suggestChildren(current, sender, input));

        return List.copyOf(suggestions);
    }

    private static @NotNull @Unmodifiable List<String> completeBranches(@NotNull List<BukkitArgument<?, ?>> candidates, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last, @NotNull Trail trail) {
        Set<String> result = new LinkedHashSet<>();

        for (BukkitArgument<?, ?> candidate : candidates) {
            if (!candidate.canUse(sender)) {
                continue;
            }

            if (from == last) {
                result.addAll(suggest(candidate, sender, args[last], "", trail.arguments()));
            } else {
                result.addAll(completeNode(candidate, sender, args, from, last, trail));
            }
        }

        return List.copyOf(result);
    }

    private static @NotNull List<String> completeNode(@NotNull BukkitArgument<?, ?> node, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last, @NotNull Trail trail) {
        int end;
        Trail next = trail;

        if (node instanceof LiteralArgument) {
            if (!args[from].equals(node.getName())) {
                return List.of();
            }

            end = from + 1;
        } else {
            end = ArgumentTokens.advance(node, from, Integer.MAX_VALUE);

            if (last < end) {
                return suggestFrom(node, sender, args, from, last, trail.arguments());
            }

            String input = joined(args, from, end - 1);

            if (!LegacyFixedValues.allows(node, input)) {
                return List.of();
            }

            try {
                Object value = node.parse(sender, input);

                if (node.isListed()) {
                    next = trail.with(node.getName(), new ParsedArgument<>(input, value));
                }
            } catch (ArgumentParseException e) {
                return List.of();
            }
        }

        return completeBranches(LegacyArgumentsParser.nodes(node.getArguments()), sender, args, end, last, next);
    }

    private static @NotNull List<String> suggestFrom(@NotNull BukkitArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last, @NotNull Supplier<CommandArguments<BukkitCommandDefinition, CommandSender>> previous) {
        String prefix = from == last ? "" : joined(args, from, last - 1) + " ";

        return suggest(argument, sender, joined(args, from, last), prefix, previous);
    }

    private static @NotNull List<String> suggest(@NotNull BukkitArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String input, @NotNull String prefix, @NotNull Supplier<CommandArguments<BukkitCommandDefinition, CommandSender>> previous) {
        if (!argument.canUse(sender)) {
            return List.of();
        }

        String lowered = input.toLowerCase();

        if (argument instanceof LiteralArgument) {
            return prefix.isEmpty() && argument.getName().toLowerCase().startsWith(lowered) ? List.of(argument.getName()) : List.of();
        }

        Set<String> immediate = new LinkedHashSet<>();
        Optional<LiteralValues> fixed = argument.getFixedValues();

        if (fixed.isPresent() && argument.getSuggestionsSupplierReplacer().isEmpty()) {
            immediate.addAll(matching(fixed.get().get().stream(), lowered, prefix));
        }

        SuggestionContext<CommandSender> context = SuggestionContext.lazy(sender, null, input, previous, false);

        if (argument.getCustomSuggestions().stream().anyMatch(BukkitSuggestionsSupplier.Async.class::isInstance)) {
            context.getPreviousArgs();
        }

        for (BukkitSuggestionsSupplier<?> supplier : argument.getCustomSuggestions()) {
            boolean async = supplier instanceof BukkitSuggestionsSupplier.Async;

            if (async && !AsyncCommandSuggestEvent.hasListeners()) throw new UnsupportedOperationException("Async suggestions are not supported on bukkit or they were disabled.");

            if (async && ASYNC.get() == null) continue;

            CompletableFuture<Collection<ArgumentSuggestion>> future = BukkitSuggestionsSupplier.resolveRaw(supplier, context);

            if (!async && future.isDone()) {
                Collection<ArgumentSuggestion> result = future.join();

                if (result != null) {
                    immediate.addAll(matching(result.stream().map(ArgumentSuggestion::getSuggestion), lowered, prefix));
                }

                continue;
            }

            List<CompletableFuture<List<String>>> collector = ASYNC.get();

            if (collector == null) continue;

            collector.add(future.handle((result, error) -> error != null || result == null
                    ? List.of()
                    : matching(result.stream().map(ArgumentSuggestion::getSuggestion), lowered, prefix)));
        }

        return new ArrayList<>(immediate);
    }

    private static @NotNull List<String> matching(@NotNull Stream<String> values, @NotNull String lowered, @NotNull String prefix) {
        Stream<String> matched = values.filter(value -> value.toLowerCase().startsWith(lowered));

        if (prefix.isEmpty()) {
            return matched.collect(Collectors.toList());
        }

        return matched.filter(value -> value.startsWith(prefix))
                .map(value -> value.substring(prefix.length()))
                .collect(Collectors.toUnmodifiableList());
    }

    private static @NotNull @Unmodifiable List<String> suggestChildren(@NotNull BukkitCommandDefinition current, @NotNull CommandSender sender, @NotNull String input) {
        String prefix = input.toLowerCase();

        return current.getChildren()
                .stream()
                .filter(child -> child.canExecute(sender))
                .map(AbstractCommandDefinition::getName)
                .filter(name -> name.toLowerCase().startsWith(prefix))
                .collect(Collectors.toUnmodifiableList());
    }

    private static @Nullable Located locate(@NotNull List<BukkitArgument<?, ?>> arguments, int cursor) {
        int position = 0;

        for (BukkitArgument<?, ?> argument : arguments) {
            int end = ArgumentTokens.advance(argument, position, Integer.MAX_VALUE);

            if (cursor < end) {
                return new Located(argument, position);
            }

            position = end;
        }

        return null;
    }

    private static @NotNull String joined(@NotNull String @NotNull [] args, int from, int to) {
        return String.join(" ", Arrays.copyOfRange(args, from, to + 1));
    }

    private static @NotNull String input(@NotNull String label, @NotNull String @NotNull [] args) {
        return args.length == 0 ? label : label + " " + String.join(" ", args);
    }

    private static final class Trail {

        private final BukkitCommandDefinition definition;
        private final CommandSender sender;
        private final String[] args;
        private final int offset;
        private final int end;
        private final CommandArguments<BukkitCommandDefinition, CommandSender> parent;
        private final String fullInput;
        private final Map<String, ParsedArgument<?>> branchValues;

        private Trail(@NotNull BukkitCommandDefinition definition, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int offset, int end, @Nullable CommandArguments<BukkitCommandDefinition, CommandSender> parent, @NotNull String fullInput) {
            this(definition, sender, args, offset, end, parent, fullInput, Map.of());
        }

        private Trail(@NotNull BukkitCommandDefinition definition, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int offset, int end, @Nullable CommandArguments<BukkitCommandDefinition, CommandSender> parent, @NotNull String fullInput, @NotNull Map<String, ParsedArgument<?>> branchValues) {
            this.definition = definition;
            this.sender = sender;
            this.args = args;
            this.offset = offset;
            this.end = end;
            this.parent = parent;
            this.fullInput = fullInput;
            this.branchValues = branchValues;
        }

        @NotNull Trail with(@NotNull String name, @NotNull ParsedArgument<?> value) {
            Map<String, ParsedArgument<?>> values = new HashMap<>(branchValues);
            values.put(name, value);

            return new Trail(definition, sender, args, offset, end, parent, fullInput, values);
        }

        @NotNull Supplier<CommandArguments<BukkitCommandDefinition, CommandSender>> arguments() {
            return () -> LegacyArgumentsParser.parsePartial(sender, definition, args, offset, end, branchValues, parent, fullInput);
        }

    }

    private static final class Located {

        private final BukkitArgument<?, ?> argument;
        private final int start;

        private Located(@NotNull BukkitArgument<?, ?> argument, int start) {
            this.argument = argument;
            this.start = start;
        }

    }

}