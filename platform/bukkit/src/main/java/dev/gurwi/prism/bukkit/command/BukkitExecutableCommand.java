package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.argument.ParsedArgument;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.base.LiteralValues;
import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.argument.StructureRotationArgument;
import dev.gurwi.prism.bukkit.argument.parser.ArgumentTokens;
import dev.gurwi.prism.bukkit.argument.parser.LegacyArgumentsParser;
import dev.gurwi.prism.bukkit.argument.suggestion.ArgumentSuggestion;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestionsSupplier;
import dev.gurwi.prism.bukkit.event.AsyncCommandSuggestEvent;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentParseException;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.exception.CommandExecutionException;
import dev.gurwi.prism.exception.NoExecutorException;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BukkitExecutableCommand extends BukkitCommand implements CommandExecutor, TabCompleter, PluginIdentifiableCommand {

    private static final ThreadLocal<List<CompletableFuture<List<String>>>> ASYNC = new ThreadLocal<>();

    private final Plugin plugin;
    private final BukkitCommandDefinition definition;

    public BukkitExecutableCommand(Plugin plugin, @NotNull BukkitCommandDefinition definition) {
        super(definition.getName());

        this.plugin = plugin;
        this.definition = definition;

        definition.visitArguments((argument, embedded) -> argument.applyLegacy());

        setAliases(definition.getAliases());
        setDescription(definition.getDescription());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!testPermission(sender)) return false;

        List<Level> levels = resolve(sender, args);

        CommandArguments<BukkitCommandDefinition, CommandSender> parsed = null;
        BukkitArgument<?, ?> node = null;

        for (Level level : levels) {
            BukkitArgumentParseException invalid = checkFixedValues(sender, level, args);

            if (invalid != null) {
                level.definition().getLegacyCommandErrorHandler().onArgumentsParseError(level.definition(), sender, invalid);
                return false;
            }
        }

        String fullInput = args.length == 0 ? label : label + " " + String.join(" ", args);

        for (Level level : levels) {
            try {
                LegacyArgumentsParser.TreeParseResult result = LegacyArgumentsParser.parseTree(sender, level.definition(), level.args(), args, level.start(), parsed, fullInput);

                parsed = result.arguments();
                node = result.node();
            } catch (BukkitArgumentParseException e) {
                level.definition().getLegacyCommandErrorHandler().onArgumentsParseError(level.definition(), sender, e);
                return false;
            }
        }

        BukkitCommandDefinition target = levels.get(levels.size() - 1).definition();
        CommandArguments<BukkitCommandDefinition, CommandSender> finalArguments = parsed;

        Collection<BukkitCommandExecutor<? extends CommandSender>> executors;

        if (node == null) {
            if (!target.hasExecutor(sender)) {
                throw new NoExecutorException(sender.getClass());
            }

            executors = target.getExecutors();
        } else {
            if (!node.hasExecutor(sender)) {
                throw new NoExecutorException(sender.getClass());
            }

            executors = node.getExecutors();
        }

        try {
            executors.forEach(executor -> executor.tryInvoke(sender, finalArguments));
        } catch (CommandExecutionException e) {
            target.getLegacyCommandErrorHandler().onCommandExecutionError(target, sender, e);
        }

        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return execute(sender, label, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (args.length == 0 || !testPermission(sender)) return List.of();
        return complete(definition, sender, args, 0, null, input(alias, args));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tabComplete(sender, alias, args);
    }

    public @NotNull Optional<CompletableFuture<List<String>>> completeAsync(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 0 || !testPermission(sender)) return Optional.empty();

        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        List<String> immediate;

        ASYNC.set(futures);

        try {
            immediate = complete(definition, sender, args, 0, null, input(definition.getName(), args));
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

    @Override
    public boolean testPermission(@NotNull CommandSender target) {
        return definition.canExecute(target);
    }

    @Override
    public boolean testPermissionSilent(@NotNull CommandSender target) {
        return testPermission(target);
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    public BukkitCommandDefinition getDefinition() {
        return definition;
    }

    private @NotNull List<Level> resolve(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        List<Level> levels = new ArrayList<>();

        BukkitCommandDefinition current = definition;
        int position = 0;

        while (true) {
            int start = position;
            List<BukkitArgument<?, ?>> required = current.getRequiredArguments();
            List<BukkitArgument<?, ?>> optional = current.getOptionalArguments();

            int requiredTokens = 0;

            for (BukkitArgument<?, ?> argument : required) {
                requiredTokens += ArgumentTokens.consumption(argument);
            }

            if (args.length - start < requiredTokens) {
                levels.add(new Level(current, Arrays.copyOfRange(args, start, args.length), start));
                return levels;
            }

            for (BukkitArgument<?, ?> argument : required) {
                position = Math.min(ArgumentTokens.advance(argument, position, args.length), args.length);
            }

            int optionalIndex = 0;
            Optional<BukkitCommandDefinition> child = Optional.empty();

            while (position < args.length) {
                child = findChild(current, sender, args[position]);

                if (child.isPresent() || optionalIndex >= optional.size()) {
                    break;
                }

                position = Math.min(ArgumentTokens.advance(optional.get(optionalIndex), position, args.length), args.length);
                optionalIndex++;
            }

            if (child.isEmpty()) {
                levels.add(new Level(current, Arrays.copyOfRange(args, start, args.length), start));
                return levels;
            }

            levels.add(new Level(current, Arrays.copyOfRange(args, start, position), start));

            current = child.get();
            position++;
        }
    }

    private @NotNull Optional<BukkitCommandDefinition> findChild(@NotNull BukkitCommandDefinition current, @NotNull CommandSender sender, @NotNull String token) {
        return current.getChild(token.toLowerCase())
                .filter(candidate -> candidate.canExecute(sender));
    }

    private @Nullable BukkitArgumentParseException checkFixedValues(@NotNull CommandSender sender, @NotNull Level level, @NotNull String @NotNull [] all) {
        String[] tokens = level.args();
        List<BukkitArgument<?, ?>> arguments = new ArrayList<>(level.definition().getRequiredArguments());
        arguments.addAll(level.definition().getOptionalArguments());

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

            if (!allowsFixedValue(argument, input)) {
                return invalidFixedValue(new FixedValueFailure(argument, input, level.start() + position), all);
            }

            position = end;
        }

        if (position >= tokens.length) {
            return null;
        }

        FixedValueFailure failure = checkBranches(sender, LegacyArgumentsParser.nodes(level.definition().getArgumentTreeBranches()), tokens, position, level.start());

        return failure == null ? null : invalidFixedValue(failure, all);
    }

    private @Nullable FixedValueFailure checkBranches(@NotNull CommandSender sender, @NotNull List<BukkitArgument<?, ?>> candidates, @NotNull String @NotNull [] tokens, int position, int base) {
        if (position >= tokens.length) {
            return null;
        }

        FixedValueFailure deepest = null;

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

                if (!allowsFixedValue(candidate, input)) {
                    FixedValueFailure failure = new FixedValueFailure(candidate, input, base + position);

                    if (deepest == null || failure.position > deepest.position) {
                        deepest = failure;
                    }

                    continue;
                }
            }

            FixedValueFailure child = checkBranches(sender, LegacyArgumentsParser.nodes(candidate.getArguments()), tokens, end, base);

            if (child == null) {
                return null;
            }

            if (deepest == null || child.position >= deepest.position) {
                deepest = child;
            }
        }

        return deepest;
    }

    private boolean allowsFixedValue(@NotNull BukkitArgument<?, ?> argument, @NotNull String input) {
        Optional<LiteralValues> fixed = argument.getFixedValues();

        if (fixed.isEmpty()) {
            return true;
        }

        if (argument instanceof StructureRotationArgument && input.equals("180")) {
            return true;
        }

        String stripped = input.regionMatches(true, 0, "minecraft:", 0, 10) ? input.substring(10) : input;

        for (String value : fixed.get().get()) {
            if (value.equalsIgnoreCase(input) || value.equalsIgnoreCase(stripped)) {
                return true;
            }
        }

        return false;
    }

    private @NotNull BukkitArgumentParseException invalidFixedValue(@NotNull FixedValueFailure failure, @NotNull String @NotNull [] all) {
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

    private @Nullable Located locate(@NotNull List<BukkitArgument<?, ?>> arguments, int cursor) {
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

    private @NotNull String joined(@NotNull String @NotNull [] args, int from, int to) {
        return String.join(" ", Arrays.copyOfRange(args, from, to + 1));
    }

    private static @NotNull String input(@NotNull String label, @NotNull String @NotNull [] args) {
        return args.length == 0 ? label : label + " " + String.join(" ", args);
    }

    private @NotNull List<String> complete(@NotNull BukkitCommandDefinition current, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int offset, @Nullable CommandArguments<BukkitCommandDefinition, CommandSender> parent, @NotNull String fullInput) {
        List<BukkitArgument<?, ?>> required = current.getRequiredArguments();
        List<BukkitArgument<?, ?>> optional = current.getOptionalArguments();
        List<BukkitArgument<?, ?>> branches = LegacyArgumentsParser.nodes(current.getArgumentTreeBranches());

        int last = args.length - 1;
        String input = args[last];

        Located located = locate(required, last - offset);

        if (located != null) {
            int start = offset + located.start();

            return suggestFrom(located.argument(), sender, args, start, last, new Trail(current, sender, args, offset, start, parent, fullInput).arguments());
        }

        int position = offset;

        for (BukkitArgument<?, ?> argument : required) {
            position = ArgumentTokens.advance(argument, position, Integer.MAX_VALUE);
        }

        int optionalIndex = 0;

        while (position < last) {
            Optional<BukkitCommandDefinition> child = findChild(current, sender, args[position]);

            if (child.isPresent()) {
                return complete(child.get(), sender, args, position + 1, new Trail(current, sender, args, offset, position, parent, fullInput).arguments().get(), fullInput);
            }

            if (optionalIndex >= optional.size()) {
                return completeBranches(branches, sender, args, position, last, new Trail(current, sender, args, offset, position, parent, fullInput));
            }

            BukkitArgument<?, ?> next = optional.get(optionalIndex);
            int end = ArgumentTokens.advance(next, position, Integer.MAX_VALUE);

            if (last < end) {
                return suggestFrom(next, sender, args, position, last, new Trail(current, sender, args, offset, position, parent, fullInput).arguments());
            }

            position = end;
            optionalIndex++;
        }

        Set<String> suggestions = new LinkedHashSet<>();

        Trail trail = new Trail(current, sender, args, offset, position, parent, fullInput);

        if (optionalIndex < optional.size()) {
            suggestions.addAll(suggest(optional.get(optionalIndex), sender, input, trail.arguments()));
        } else {
            suggestions.addAll(completeBranches(branches, sender, args, last, last, trail));
        }

        suggestions.addAll(suggestChildren(current, sender, input));

        return List.copyOf(suggestions);
    }

    private @NotNull @Unmodifiable List<String> completeBranches(@NotNull List<BukkitArgument<?, ?>> candidates, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last, @NotNull Trail trail) {
        Set<String> result = new LinkedHashSet<>();

        for (BukkitArgument<?, ?> candidate : candidates) {
            if (!candidate.canUse(sender)) {
                continue;
            }

            if (from == last) {
                result.addAll(suggest(candidate, sender, args[last], trail.arguments()));
            } else {
                result.addAll(completeNode(candidate, sender, args, from, last, trail));
            }
        }

        return List.copyOf(result);
    }

    private @NotNull List<String> completeNode(@NotNull BukkitArgument<?, ?> node, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last, @NotNull Trail trail) {
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

            if (!allowsFixedValue(node, input)) {
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

    private @NotNull List<String> suggestFrom(@NotNull BukkitArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last, @NotNull Supplier<CommandArguments<BukkitCommandDefinition, CommandSender>> previous) {
        String prefix = from == last ? "" : joined(args, from, last - 1) + " ";

        return suggest(argument, sender, joined(args, from, last), prefix, previous);
    }

    private @NotNull List<String> suggest(@NotNull BukkitArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String input, @NotNull Supplier<CommandArguments<BukkitCommandDefinition, CommandSender>> previous) {
        return suggest(argument, sender, input, "", previous);
    }

    private @NotNull List<String> suggest(@NotNull BukkitArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String input, @NotNull String prefix, @NotNull Supplier<CommandArguments<BukkitCommandDefinition, CommandSender>> previous) {
        if (!argument.canUse(sender)) {
            return List.of();
        }

        String lowered = input.toLowerCase();

        if (argument instanceof LiteralArgument) {
            return prefix.isEmpty() && argument.getName().toLowerCase().startsWith(lowered) ? List.of(argument.getName()) : List.of();
        }

        Optional<LiteralValues> fixed = argument.getFixedValues();

        if (fixed.isPresent()) {
            return strip(fixed.get().get().stream()
                    .filter(value -> value.toLowerCase().startsWith(lowered))
                    .collect(Collectors.toList()), prefix);
        }

        SuggestionContext<CommandSender> context = SuggestionContext.lazy(sender, null, input, previous, false);
        Set<String> immediate = new LinkedHashSet<>();

        if (argument.getCustomSuggestions().stream().anyMatch(BukkitSuggestionsSupplier.Async.class::isInstance)) {
            context.getPreviousArgs();
        }

        for (BukkitSuggestionsSupplier<?> suggestions : argument.getCustomSuggestions()) {
            if (suggestions instanceof BukkitSuggestionsSupplier.Async) {
                BukkitSuggestionsSupplier.Async async = (BukkitSuggestionsSupplier.Async) suggestions;

                if (!AsyncCommandSuggestEvent.hasListeners()) throw new UnsupportedOperationException("Async suggestions are not supported on bukkit or they were disabled.");

                List<CompletableFuture<List<String>>> collector = ASYNC.get();

                if (collector == null) continue;

                collector.add(async.getSuggestions(context).handle((result, error) -> {
                    if (error != null || result == null) return List.of();

                    return strip(result.stream()
                            .map(ArgumentSuggestion::getSuggestion)
                            .filter(text -> text.toLowerCase().startsWith(lowered))
                            .collect(Collectors.toList()), prefix);
                }));

                continue;
            }

            if (suggestions instanceof BukkitSuggestionsSupplier.Sync) {
                BukkitSuggestionsSupplier.Sync sync = (BukkitSuggestionsSupplier.Sync) suggestions;
                Collection<ArgumentSuggestion> result = sync.getSuggestions(context);

                if (result == null) continue;

                immediate.addAll(strip(result.stream()
                        .map(ArgumentSuggestion::getSuggestion)
                        .filter(text -> text.toLowerCase().startsWith(lowered))
                        .collect(Collectors.toList()), prefix));
            }
        }

        return new ArrayList<>(immediate);
    }

    private @NotNull List<String> strip(@NotNull List<String> values, @NotNull String prefix) {
        if (prefix.isEmpty()) return values;

        return values.stream()
                .filter(value -> value.startsWith(prefix))
                .map(value -> value.substring(prefix.length()))
                .collect(Collectors.toUnmodifiableList());
    }

    private @NotNull @Unmodifiable List<String> suggestChildren(@NotNull BukkitCommandDefinition current, @NotNull CommandSender sender, @NotNull String input) {
        String prefix = input.toLowerCase();

        return current.getChildren()
                .stream()
                .filter(child -> child.canExecute(sender))
                .map(AbstractCommandDefinition::getName)
                .filter(name -> name.toLowerCase().startsWith(prefix))
                .collect(Collectors.toUnmodifiableList());
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

    private static final class Level {

        private final BukkitCommandDefinition definition;
        private final String[] args;
        private final int start;

        private Level(@NotNull BukkitCommandDefinition definition, @NotNull String @NotNull [] args, int start) {
            this.definition = definition;
            this.args = args;
            this.start = start;
        }

        @NotNull BukkitCommandDefinition definition() {
            return definition;
        }

        @NotNull String @NotNull [] args() {
            return args;
        }

        int start() {
            return start;
        }
    }

    private static final class FixedValueFailure {

        private final BukkitArgument<?, ?> argument;
        private final String input;
        private final int position;

        private FixedValueFailure(@NotNull BukkitArgument<?, ?> argument, @NotNull String input, int position) {
            this.argument = argument;
            this.input = input;
            this.position = position;
        }

    }

    private static final class Located {

        private final BukkitArgument<?, ?> argument;
        private final int start;

        private Located(@NotNull BukkitArgument<?, ?> argument, int start) {
            this.argument = argument;
            this.start = start;
        }

        @NotNull BukkitArgument<?, ?> argument() {
            return argument;
        }

        int start() {
            return start;
        }
    }

}