package dev.gurwi.prism.velocity.adapter;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import dev.gurwi.prism.brigadier.BrigadierAdapter;
import dev.gurwi.prism.brigadier.BrigadierTreeBuilder;
import dev.gurwi.prism.brigadier.NodeSpec;
import dev.gurwi.prism.velocity.argument.LiteralArgument;
import dev.gurwi.prism.velocity.argument.base.LiteralValues;
import dev.gurwi.prism.velocity.argument.base.VelocityArgument;
import dev.gurwi.prism.velocity.argument.ArgumentSuggestion;
import dev.gurwi.prism.velocity.command.VelocityCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VelocityCommandDefinitionAdapter implements BrigadierAdapter<CommandSource, VelocityCommandDefinition> {

    private static final SimpleCommandExceptionType NO_EXECUTOR = new SimpleCommandExceptionType(new LiteralMessage("This command cannot be run by this sender"));

    private final BrigadierTreeBuilder<CommandSource, VelocityCommandDefinition> builder = new BrigadierTreeBuilder<>(this);

    public BrigadierTreeBuilder<CommandSource, VelocityCommandDefinition> getBuilder() {
        return builder;
    }

    @Override
    public boolean visible(@NotNull VelocityCommandDefinition definition, @NotNull CommandSource source) {
        if (!definition.canExecute(source)) return false;
        if (definition.hasExecutor(source)) return true;

        for (AbstractArgument<?, ?, ?, ?> branch : branches(definition)) {
            if (usable((VelocityArgument<?, ?>) branch, source)) {
                return true;
            }
        }

        return definition.getChildren().stream().anyMatch(child -> visible(child, source));
    }

    @Override
    public boolean available(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull CommandSource source) {
        return argument instanceof VelocityArgument<?, ?> velocityArgument && velocityArgument.canUse(source);
    }

    @Override
    public @Nullable ArgumentType<?> nativeType(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (!(argument instanceof VelocityArgument<?, ?> velocityArgument) || argument instanceof LiteralArgument) {
            return null;
        }

        return velocityArgument.getArgumentType();
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
    public boolean accepts(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull CommandSource source, @NotNull String word) {
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
    public @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> branches(@NotNull VelocityCommandDefinition definition) {
        return arguments(definition.getArgumentTreeBranches());
    }

    @Override
    public @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> children(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return arguments(((VelocityArgument<?, ?>) argument).getArguments());
    }

    @Override
    public @Nullable Command<CommandSource> command(@NotNull VelocityCommandDefinition definition) {
        return command(definition, Map.of());
    }

    @Override
    public @Nullable Command<CommandSource> command(@NotNull VelocityCommandDefinition definition, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        return ctx -> execute(definition, forced, ctx);
    }

    @Override
    public @Nullable Command<CommandSource> command(@NotNull VelocityCommandDefinition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path) {
        return command(definition, path, Map.of());
    }

    @Override
    public @Nullable Command<CommandSource> command(@NotNull VelocityCommandDefinition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        VelocityArgument<?, ?> last = (VelocityArgument<?, ?>) path.getLast();

        if (last.getExecutors().isEmpty()) {
            return null;
        }

        return ctx -> executeNode(definition, path, forced, ctx);
    }

    @Override
    public @NotNull List<String> forcedValues(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (!(argument instanceof VelocityArgument<?, ?> velocityArgument) || argument instanceof LiteralArgument) {
            return List.of();
        }

        return velocityArgument.getFixedValues()
                .map(LiteralValues::get)
                .orElse(List.of());
    }

    @Override
    public @Nullable SuggestionProvider<CommandSource> suggestions(@NotNull AbstractArgument<?, ?, ?, ?> argument, @Nullable ArgumentType<?> nativeType) {
        if (!(argument instanceof VelocityArgument<?, ?> velocityArgument) || argument instanceof LiteralArgument) {
            return null;
        }

        List<SuggestionsSupplier<CommandSource, CompletableFuture<Collection<ArgumentSuggestion>>>> suppliers = velocityArgument.getCustomSuggestions();

        if (suppliers.isEmpty()) {
            return null;
        }

        boolean nativeProvided = BrigadierAdapter.providesSuggestions(nativeType);

        return (context, suggestionsBuilder) -> {
            String remaining = suggestionsBuilder.getRemaining();
            String lowered = remaining.toLowerCase(Locale.ROOT);

            SuggestionContext<CommandSource> suggestionContext =
                    new SuggestionContext<>(context.getSource(), suggestionsBuilder.getInput(), remaining, nativeProvided);

            List<CompletableFuture<Collection<ArgumentSuggestion>>> parts = new ArrayList<>(suppliers.size());

            for (SuggestionsSupplier<CommandSource, CompletableFuture<Collection<ArgumentSuggestion>>> supplier : suppliers) {
                CompletableFuture<Collection<ArgumentSuggestion>> supplied = supplier.getSuggestions(suggestionContext);

                if (supplied == null) {
                    parts.add(CompletableFuture.completedFuture(List.of()));
                } else {
                    parts.add(supplied.exceptionally(error -> List.of()));
                }
            }

            return CompletableFuture.allOf(parts.toArray(new CompletableFuture[0])).thenCompose(ignored -> {
                Set<String> seen = new HashSet<>();
                boolean added = false;

                for (CompletableFuture<Collection<ArgumentSuggestion>> part : parts) {
                    Collection<ArgumentSuggestion> suggestions = part.join();

                    if (suggestions == null) continue;

                    for (ArgumentSuggestion suggestion : suggestions) {
                        String text = suggestion.getSuggestion();

                        if (!text.toLowerCase(Locale.ROOT).startsWith(lowered) || !seen.add(text)) continue;

                        suggestion.getTooltip().ifPresentOrElse(
                                tooltip -> suggestionsBuilder.suggest(text, VelocityBrigadierMessage.tooltip(tooltip)),
                                () -> suggestionsBuilder.suggest(text)
                        );
                        added = true;
                    }
                }

                if (!added && nativeType != null) {
                    return nativeType.listSuggestions(context, suggestionsBuilder);
                }

                return suggestionsBuilder.buildFuture();
            });
        };
    }

    private int execute(@NotNull VelocityCommandDefinition target, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced, @NotNull CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource sender = context.getSource();

        if (!target.hasExecutor(sender)) {
            throw NO_EXECUTOR.create();
        }

        CommandArguments<VelocityCommandDefinition, CommandSource> parsed = parseHierarchy(target, context, List.of(), forced);

        target.getExecutors().forEach(executor -> executor.tryInvoke(sender, parsed));

        return Command.SINGLE_SUCCESS;
    }

    private int executeNode(@NotNull VelocityCommandDefinition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced, @NotNull CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource sender = context.getSource();
        VelocityArgument<?, ?> last = (VelocityArgument<?, ?>) path.getLast();

        if (!last.hasExecutor(sender)) {
            throw NO_EXECUTOR.create();
        }

        CommandArguments<VelocityCommandDefinition, CommandSource> parsed = parseHierarchy(definition, context, path, forced);

        last.getExecutors().forEach(executor -> executor.tryInvoke(sender, parsed));

        return Command.SINGLE_SUCCESS;
    }

    private @Nullable CommandArguments<VelocityCommandDefinition, CommandSource> parseHierarchy(@NotNull VelocityCommandDefinition target, @NotNull CommandContext<CommandSource> context, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) throws CommandSyntaxException {
        Deque<VelocityCommandDefinition> hierarchy = new ArrayDeque<>();
        VelocityCommandDefinition current = target;

        while (current != null) {
            hierarchy.addFirst(current);
            current = current.getParent().orElse(null);
        }

        CommandArguments<VelocityCommandDefinition, CommandSource> parsed = null;

        for (VelocityCommandDefinition level : hierarchy) {
            parsed = parseLevel(level, context, parsed, level == target ? path : List.of(), forced);
        }

        return parsed;
    }

    private @NotNull CommandArguments<VelocityCommandDefinition, CommandSource> parseLevel(@NotNull VelocityCommandDefinition level, @NotNull CommandContext<CommandSource> context, CommandArguments<VelocityCommandDefinition, CommandSource> parent, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) throws CommandSyntaxException {
        Map<AbstractArgument<?, ?, ?, ?>, List<NodeSpec>> grouped = new LinkedHashMap<>();

        for (NodeSpec spec : builder.specs(level)) {
            grouped.computeIfAbsent(spec.argument(), _ -> new ArrayList<>()).add(spec);
        }

        Map<String, Object> required = new HashMap<>();
        Map<String, Object> optional = new HashMap<>();

        for (Map.Entry<AbstractArgument<?, ?, ?, ?>, List<NodeSpec>> entry : grouped.entrySet()) {
            VelocityArgument<?, ?> argument = (VelocityArgument<?, ?>) entry.getKey();
            NodeSpec spec = entry.getValue().getFirst();

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
            VelocityArgument<?, ?> argument = (VelocityArgument<?, ?>) path.get(index);

            if (literal(argument) || !argument.isListed()) {
                continue;
            }

            String fixed = forced.get(argument);

            if (fixed != null) {
                required.put(argument.getName(), parseFixed(argument, fixed));
                continue;
            }

            List<NodeSpec> specs = builder.treeSpecs(level, path.subList(0, index + 1));
            required.put(argument.getName(), read(context, specs.getFirst().name()));
        }

        return new CommandArguments<>(level, required, optional, parent);
    }

    private @NotNull Object parseFixed(@NotNull VelocityArgument<?, ?> argument, @NotNull String value) throws CommandSyntaxException {
        return argument.getArgumentType().parse(new StringReader(value));
    }

    private @NotNull Object read(@NotNull CommandContext<CommandSource> context, @NotNull String node) {
        return context.getArgument(node, Object.class);
    }

    private boolean present(@NotNull CommandContext<CommandSource> context, @NotNull String node) {
        try {
            context.getArgument(node, Object.class);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean usable(@NotNull VelocityArgument<?, ?> node, @NotNull CommandSource source) {
        if (!node.canUse(source)) return false;
        if (node.hasExecutor(source)) return true;

        for (AbstractArgument<?, ?, ?, ?> child : children(node)) {
            if (usable((VelocityArgument<?, ?>) child, source)) return true;
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