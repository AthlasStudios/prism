package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.argument.parser.ArgumentTokens;
import dev.gurwi.prism.bukkit.argument.parser.LegacyArgumentsParser;
import dev.gurwi.prism.bukkit.argument.suggestion.CommandSuggestion;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestionsSupplier;
import dev.gurwi.prism.bukkit.event.AsyncCommandSuggestEvent;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.exception.NoExecutorException;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BukkitExecutableCommand extends BukkitCommand implements CommandExecutor, TabCompleter, PluginIdentifiableCommand {

    private static final ThreadLocal<List<CompletableFuture<List<String>>>> ASYNC = new ThreadLocal<>();

    private final Plugin plugin;
    private final BukkitCommandDefinition definition;

    public BukkitExecutableCommand(Plugin plugin, @NotNull BukkitCommandDefinition definition) {
        super(definition.getName());

        this.plugin = plugin;
        this.definition = definition;

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
            try {
                LegacyArgumentsParser.TreeParseResult result = LegacyArgumentsParser.parseTree(sender, level.definition(), level.args(), args, level.start(), parsed);

                parsed = result.arguments();
                node = result.node();
            } catch (BukkitArgumentsParseException e) {
                level.definition().getLegacyCommandErrorHandler().onArgumentsParseError(level.definition(), sender, e);
                return false;
            }
        }

        BukkitCommandDefinition target = levels.get(levels.size() - 1).definition();
        CommandArguments<BukkitCommandDefinition, CommandSender> finalArguments = parsed;

        if (node == null) {
            if (!target.hasExecutor(sender)) {
                throw new NoExecutorException(sender.getClass());
            }

            target.getExecutors().forEach(executor -> executor.tryInvoke(sender, finalArguments));

            return true;
        }

        if (!node.hasExecutor(sender)) {
            throw new NoExecutorException(sender.getClass());
        }

        node.getExecutors().forEach(executor -> executor.tryInvoke(sender, finalArguments));

        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return execute(sender, label, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (args.length == 0) return List.of();
        return complete(definition, sender, args, 0);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tabComplete(sender, alias, args);
    }

    public @NotNull Optional<CompletableFuture<List<String>>> completeAsync(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 0) return Optional.empty();

        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        List<String> immediate;

        ASYNC.set(futures);

        try {
            immediate = complete(definition, sender, args, 0);
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

    private @NotNull List<String> complete(@NotNull BukkitCommandDefinition current, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int offset) {
        List<BukkitArgument<?, ?>> required = current.getRequiredArguments();
        List<BukkitArgument<?, ?>> optional = current.getOptionalArguments();
        List<BukkitArgument<?, ?>> branches = nodes(current.getArgumentTreeBranches());

        int last = args.length - 1;
        String input = args[last];

        Located located = locate(required, last - offset);

        if (located != null) {
            return suggestFrom(located.argument(), sender, args, offset + located.start(), last);
        }

        int position = offset;

        for (BukkitArgument<?, ?> argument : required) {
            position = ArgumentTokens.advance(argument, position, Integer.MAX_VALUE);
        }

        int optionalIndex = 0;

        while (position < last) {
            Optional<BukkitCommandDefinition> child = findChild(current, sender, args[position]);

            if (child.isPresent()) {
                return complete(child.get(), sender, args, position + 1);
            }

            if (optionalIndex >= optional.size()) {
                return completeBranches(branches, sender, args, position, last);
            }

            BukkitArgument<?, ?> next = optional.get(optionalIndex);
            int end = ArgumentTokens.advance(next, position, Integer.MAX_VALUE);

            if (last < end) {
                return suggestFrom(next, sender, args, position, last);
            }

            position = end;
            optionalIndex++;
        }

        Set<String> suggestions = new LinkedHashSet<>();

        if (optionalIndex < optional.size()) {
            suggestions.addAll(suggest(optional.get(optionalIndex), sender, input));
        } else {
            suggestions.addAll(completeBranches(branches, sender, args, last, last));
        }

        suggestions.addAll(suggestChildren(current, sender, input));

        return List.copyOf(suggestions);
    }

    private @NotNull @Unmodifiable List<String> completeBranches(@NotNull List<BukkitArgument<?, ?>> candidates, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last) {
        Set<String> result = new LinkedHashSet<>();

        for (BukkitArgument<?, ?> candidate : candidates) {
            if (!candidate.canUse(sender)) {
                continue;
            }

            if (from == last) {
                result.addAll(suggestNode(candidate, sender, args[last]));
            } else {
                result.addAll(completeNode(candidate, sender, args, from, last));
            }
        }

        return List.copyOf(result);
    }

    private @NotNull List<String> completeNode(@NotNull BukkitArgument<?, ?> node, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last) {
        int end;

        if (node instanceof LiteralArgument) {
            if (!args[from].equals(node.getName())) {
                return List.of();
            }

            end = from + 1;
        } else {
            end = ArgumentTokens.advance(node, from, Integer.MAX_VALUE);

            if (last < end) {
                return suggestFrom(node, sender, args, from, last);
            }

            try {
                node.parse(sender, joined(args, from, end - 1));
            } catch (ArgumentParseException e) {
                return List.of();
            }
        }

        return completeBranches(nodes(node.getArguments()), sender, args, end, last);
    }

    private @NotNull List<String> suggestNode(@NotNull BukkitArgument<?, ?> node, @NotNull CommandSender sender, @NotNull String input) {
        if (node instanceof LiteralArgument) {
            return node.getName().toLowerCase().startsWith(input.toLowerCase()) ? List.of(node.getName()) : List.of();
        }

        return suggest(node, sender, input);
    }

    private @NotNull List<String> suggestFrom(@NotNull BukkitArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last) {
        String prefix = from == last ? "" : joined(args, from, last - 1) + " ";

        return suggest(argument, sender, joined(args, from, last), prefix);
    }

    private @NotNull List<String> suggest(@NotNull BukkitArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String input) {
        return suggest(argument, sender, input, "");
    }

    private @NotNull List<String> suggest(@NotNull BukkitArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String input, @NotNull String prefix) {
        if (!argument.canUse(sender)) {
            return List.of();
        }

        SuggestionContext<CommandSender> context = new SuggestionContext<>(sender, null, input);
        BukkitSuggestionsSupplier<?> suggestions = argument.getSuggestionsSupplier();

        if (suggestions instanceof BukkitSuggestionsSupplier.Async) {
            BukkitSuggestionsSupplier.Async async = (BukkitSuggestionsSupplier.Async) suggestions;

            if (!AsyncCommandSuggestEvent.hasListeners()) throw new UnsupportedOperationException("Async suggestions are not supported on bukkit or they were disabled.");

            List<CompletableFuture<List<String>>> collector = ASYNC.get();

            if (collector == null) return List.of();

            String lowered = input.toLowerCase();

            collector.add(async.getSuggestions(context).handle((result, error) -> {
                if (error != null || result == null) return List.of();

                return strip(result.stream()
                        .map(CommandSuggestion::getSuggestion)
                        .filter(text -> text.toLowerCase().startsWith(lowered))
                        .collect(Collectors.toList()), prefix);
            }));

            return List.of();
        }

        if (suggestions instanceof BukkitSuggestionsSupplier.Sync) {
            BukkitSuggestionsSupplier.Sync sync = (BukkitSuggestionsSupplier.Sync) suggestions;

            return strip(sync.getSuggestions(context).stream().map(CommandSuggestion::getSuggestion).collect(Collectors.toList()), prefix);
        }

        return List.of();
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

    private static @NotNull List<BukkitArgument<?, ?>> nodes(@NotNull Collection<?> source) {
        List<BukkitArgument<?, ?>> result = new ArrayList<>();

        for (Object item : source) {
            result.add((BukkitArgument<?, ?>) item);
        }

        return result;
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