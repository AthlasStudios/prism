package dev.gurwi.prism.bungeecord.adapter;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import dev.gurwi.prism.brigadier.BrigadierAdapter;
import dev.gurwi.prism.brigadier.BrigadierTreeBuilder;
import dev.gurwi.prism.brigadier.NodeSpec;
import dev.gurwi.prism.bungeecord.argument.LiteralArgument;
import dev.gurwi.prism.bungeecord.argument.base.LiteralValues;
import dev.gurwi.prism.bungeecord.argument.base.BungeecordArgument;
import dev.gurwi.prism.bungeecord.argument.ArgumentSuggestion;
import dev.gurwi.prism.bungeecord.command.BungeecordCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BungeecordCommandDefinitionAdapter implements BrigadierAdapter<CommandSender, BungeecordCommandDefinition> {

    private static final SimpleCommandExceptionType NO_EXECUTOR = new SimpleCommandExceptionType(new LiteralMessage("This command cannot be run by this sender"));

    private final BrigadierTreeBuilder<CommandSender, BungeecordCommandDefinition> builder = new BrigadierTreeBuilder<>(this);

    public BrigadierTreeBuilder<CommandSender, BungeecordCommandDefinition> getBuilder() {
        return builder;
    }

    @Override
    public boolean visible(@NotNull BungeecordCommandDefinition definition, @NotNull CommandSender source) {
        if (!definition.canExecute(source)) return false;
        if (definition.hasExecutor(source)) return true;

        for (AbstractArgument<?, ?, ?, ?> branch : branches(definition)) {
            if (usable((BungeecordArgument<?, ?>) branch, source)) {
                return true;
            }
        }

        return definition.getChildren().stream().anyMatch(child -> visible(child, source));
    }

    @Override
    public boolean available(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull CommandSender source) {
        return argument instanceof BungeecordArgument<?, ?> bungeecordArgument && bungeecordArgument.canUse(source);
    }

    @Override
    public @Nullable ArgumentType<?> nativeType(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (!(argument instanceof BungeecordArgument<?, ?> bungeecordArgument) || argument instanceof LiteralArgument) {
            return null;
        }

        return bungeecordArgument.getArgumentType();
    }

    @Override
    public int tokens(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return 1;
    }

    @Override
    public boolean greedy(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return nativeType(argument) instanceof StringArgumentType string
                && string.getType() == StringArgumentType.StringType.GREEDY_PHRASE;
    }

    @Override
    public boolean literal(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return argument instanceof LiteralArgument;
    }

    @Override
    public boolean accepts(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull CommandSender source, @NotNull String word) {
        ArgumentType<?> type = nativeType(argument);

        if (type == null) {
            return false;
        }

        StringReader reader = new StringReader(word);

        try {
            type.parse(reader);
            return !reader.canRead();
        } catch (CommandSyntaxException | RuntimeException e) {
            return false;
        }
    }

    @Override
    public @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> branches(@NotNull BungeecordCommandDefinition definition) {
        return arguments(definition.getArgumentTreeBranches());
    }

    @Override
    public @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> children(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return arguments(((BungeecordArgument<?, ?>) argument).getArguments());
    }

    @Override
    public @Nullable Command<CommandSender> command(@NotNull BungeecordCommandDefinition definition) {
        return command(definition, Map.of());
    }

    @Override
    public @Nullable Command<CommandSender> command(@NotNull BungeecordCommandDefinition definition, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        return ctx -> execute(definition, forced, ctx);
    }

    @Override
    public @Nullable Command<CommandSender> command(@NotNull BungeecordCommandDefinition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path) {
        return command(definition, path, Map.of());
    }

    @Override
    public @Nullable Command<CommandSender> command(@NotNull BungeecordCommandDefinition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        BungeecordArgument<?, ?> last = (BungeecordArgument<?, ?>) path.get(path.size() - 1);

        if (last.getExecutors().isEmpty()) {
            return null;
        }

        return ctx -> executeNode(definition, path, forced, ctx);
    }

    @Override
    public @NotNull List<String> forcedValues(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (!(argument instanceof BungeecordArgument<?, ?> bungeecordArgument) || argument instanceof LiteralArgument) {
            return List.of();
        }

        return bungeecordArgument.getFixedValues()
                .map(LiteralValues::get)
                .orElse(List.of());
    }

    @Override
    public @Nullable SuggestionProvider<CommandSender> suggestions(@NotNull AbstractArgument<?, ?, ?, ?> argument, @Nullable ArgumentType<?> nativeType) {
        if (!(argument instanceof BungeecordArgument<?, ?> bungeecordArgument) || argument instanceof LiteralArgument) {
            return null;
        }

        SuggestionsSupplier<CommandSender, CompletableFuture<Collection<ArgumentSuggestion>>> supplier = bungeecordArgument.getSuggestionsSupplier();

        return (context, suggestionsBuilder) -> {
            String remaining = suggestionsBuilder.getRemaining();
            String lowered = remaining.toLowerCase(Locale.ROOT);

            SuggestionContext<CommandSender> suggestionContext =
                    new SuggestionContext<>(context.getSource(), suggestionsBuilder.getInput(), remaining);

            CompletableFuture<Collection<ArgumentSuggestion>> supplied = supplier.getSuggestions(suggestionContext);

            if (supplied == null) {
                supplied = CompletableFuture.completedFuture(List.of());
            }

            return supplied
                    .handle((suggestions, error) -> {
                        boolean added = false;

                        if (error == null && suggestions != null) {
                            for (ArgumentSuggestion suggestion : suggestions) {
                                String text = suggestion.getSuggestion();

                                if (!text.toLowerCase(Locale.ROOT).startsWith(lowered)) continue;

                                suggestionsBuilder.suggest(text);
                                added = true;
                            }
                        }

                        if (!added && nativeType != null) {
                            return nativeType.listSuggestions(context, suggestionsBuilder);
                        }

                        return suggestionsBuilder.buildFuture();
                    })
                    .thenCompose(Function.identity());
        };
    }

    private int execute(@NotNull BungeecordCommandDefinition target, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced, @NotNull CommandContext<CommandSender> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource();

        if (!target.hasExecutor(sender)) {
            throw NO_EXECUTOR.create();
        }

        CommandArguments<BungeecordCommandDefinition, CommandSender> parsed = parseHierarchy(target, context, List.of(), forced);

        target.getExecutors().forEach(executor -> executor.tryInvoke(sender, parsed));

        return Command.SINGLE_SUCCESS;
    }

    private int executeNode(@NotNull BungeecordCommandDefinition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced, @NotNull CommandContext<CommandSender> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource();
        BungeecordArgument<?, ?> last = (BungeecordArgument<?, ?>) path.get(path.size() - 1);

        if (!last.hasExecutor(sender)) {
            throw NO_EXECUTOR.create();
        }

        CommandArguments<BungeecordCommandDefinition, CommandSender> parsed = parseHierarchy(definition, context, path, forced);

        last.getExecutors().forEach(executor -> executor.tryInvoke(sender, parsed));

        return Command.SINGLE_SUCCESS;
    }

    private @Nullable CommandArguments<BungeecordCommandDefinition, CommandSender> parseHierarchy(@NotNull BungeecordCommandDefinition target, @NotNull CommandContext<CommandSender> context, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) throws CommandSyntaxException {
        Deque<BungeecordCommandDefinition> hierarchy = new ArrayDeque<>();
        BungeecordCommandDefinition current = target;

        while (current != null) {
            hierarchy.addFirst(current);
            current = current.getParent().orElse(null);
        }

        CommandArguments<BungeecordCommandDefinition, CommandSender> parsed = null;

        for (BungeecordCommandDefinition level : hierarchy) {
            parsed = parseLevel(level, context, parsed, level == target ? path : List.of(), forced);
        }

        return parsed;
    }

    private @NotNull CommandArguments<BungeecordCommandDefinition, CommandSender> parseLevel(@NotNull BungeecordCommandDefinition level, @NotNull CommandContext<CommandSender> context, CommandArguments<BungeecordCommandDefinition, CommandSender> parent, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) throws CommandSyntaxException {
        Map<AbstractArgument<?, ?, ?, ?>, List<NodeSpec>> grouped = new LinkedHashMap<>();

        for (NodeSpec spec : builder.specs(level)) {
            grouped.computeIfAbsent(spec.argument(), ignored -> new ArrayList<>()).add(spec);
        }

        Map<String, Object> required = new HashMap<>();
        Map<String, Object> optional = new HashMap<>();

        for (Map.Entry<AbstractArgument<?, ?, ?, ?>, List<NodeSpec>> entry : grouped.entrySet()) {
            BungeecordArgument<?, ?> argument = (BungeecordArgument<?, ?>) entry.getKey();
            NodeSpec spec = entry.getValue().get(0);

            if (spec.literal() || !argument.isListed()) {
                continue;
            }

            String fixed = forced.get(argument);

            if (fixed == null && spec.optional() && !present(context, spec.name())) {
                continue;
            }

            Object value = fixed != null ? parseFixed(argument, fixed) : read(context, spec.name());

            if (spec.optional()) {
                optional.put(argument.getName(), value);
            } else {
                required.put(argument.getName(), value);
            }
        }

        for (int index = 0; index < path.size(); index++) {
            BungeecordArgument<?, ?> argument = (BungeecordArgument<?, ?>) path.get(index);

            if (literal(argument) || !argument.isListed()) {
                continue;
            }

            String fixed = forced.get(argument);

            if (fixed != null) {
                required.put(argument.getName(), parseFixed(argument, fixed));
                continue;
            }

            List<NodeSpec> specs = builder.treeSpecs(level, path.subList(0, index + 1));
            required.put(argument.getName(), read(context, specs.get(0).name()));
        }

        return new CommandArguments<>(level, required, optional, parent);
    }

    private @NotNull Object parseFixed(@NotNull BungeecordArgument<?, ?> argument, @NotNull String value) throws CommandSyntaxException {
        return argument.getArgumentType().parse(new StringReader(value));
    }

    private @NotNull Object read(@NotNull CommandContext<CommandSender> context, @NotNull String node) {
        return context.getArgument(node, Object.class);
    }

    private boolean present(@NotNull CommandContext<CommandSender> context, @NotNull String node) {
        try {
            context.getArgument(node, Object.class);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean usable(@NotNull BungeecordArgument<?, ?> node, @NotNull CommandSender source) {
        if (!node.canUse(source)) return false;
        if (node.hasExecutor(source)) return true;

        for (AbstractArgument<?, ?, ?, ?> child : children(node)) {
            if (usable((BungeecordArgument<?, ?>) child, source)) return true;
        }

        return false;
    }

    private static @NotNull List<AbstractArgument<?, ?, ?, ?>> arguments(@NotNull Collection<?> source) {
        List<AbstractArgument<?, ?, ?, ?>> result = new ArrayList<>();

        for (Object item : source) {
            result.add((AbstractArgument<?, ?, ?, ?>) item);
        }

        return result;
    }

}