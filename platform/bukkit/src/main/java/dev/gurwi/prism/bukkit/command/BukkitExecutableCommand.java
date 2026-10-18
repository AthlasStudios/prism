package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.argument.parser.ArgumentTokens;
import dev.gurwi.prism.bukkit.argument.parser.LegacyArgumentsParser;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestion;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestionsSupplier;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.exception.NoExecutorException;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.xml.transform.Source;
import java.util.*;

public class BukkitExecutableCommand extends BukkitCommand implements CommandExecutor, TabCompleter, PluginIdentifiableCommand {

    private final JavaPlugin plugin;
    private final BukkitCommandDefinition definition;

    public BukkitExecutableCommand(JavaPlugin plugin, @NotNull BukkitCommandDefinition definition) {
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

    private @NotNull List<String> completeBranches(@NotNull List<BukkitArgument<?, ?>> candidates, @NotNull CommandSender sender, @NotNull String @NotNull [] args, int from, int last) {
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
        List<String> suggestions = suggest(argument, sender, joined(args, from, last));

        if (from == last) {
            return suggestions;
        }

        String prefix = joined(args, from, last - 1) + " ";

        return suggestions.stream()
                .filter(suggestion -> suggestion.startsWith(prefix))
                .map(suggestion -> suggestion.substring(prefix.length()))
                .toList();
    }

    private @NotNull List<String> suggest(@NotNull BukkitArgument<?, ?> argument, @NotNull CommandSender sender, @NotNull String input) {
        if (!argument.canUse(sender)) {
            return List.of();
        }

        SuggestionContext<CommandSender> context = new SuggestionContext<>(sender, null, input); // TODO -> REMOVE THE NULL CURRENT INPUT ARG
        BukkitSuggestionsSupplier<?> suggestions = argument.getSuggestionsSupplier();

        if (suggestions instanceof BukkitSuggestionsSupplier.Sync sync) {
            return sync.getSuggestions(context).stream().map(BukkitSuggestion::getSuggestion).toList();
        }

        return List.of(); // TODO -> HANDLE ASYNC
        //return List.copyOf(argument.getSuggestionsSupplier().getSuggestions(context));
    }

    private @NotNull @Unmodifiable List<String> suggestChildren(@NotNull BukkitCommandDefinition current, @NotNull CommandSender sender, @NotNull String input) {
        String prefix = input.toLowerCase();

        return current.getChildren()
                .stream()
                .filter(child -> child.canExecute(sender))
                .map(AbstractCommandDefinition::getName)
                .filter(name -> name.toLowerCase().startsWith(prefix))
                .toList();
    }

    private static @NotNull List<BukkitArgument<?, ?>> nodes(@NotNull Collection<?> source) {
        List<BukkitArgument<?, ?>> result = new ArrayList<>();

        for (Object item : source) {
            result.add((BukkitArgument<?, ?>) item);
        }

        return result;
    }

    private record Level(@NotNull BukkitCommandDefinition definition, @NotNull String @NotNull [] args, int start) {
    }

    private record Located(@NotNull BukkitArgument<?, ?> argument, int start) {
    }

}