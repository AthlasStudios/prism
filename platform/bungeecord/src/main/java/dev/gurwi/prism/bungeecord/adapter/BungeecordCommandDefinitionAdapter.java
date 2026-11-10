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
import dev.gurwi.prism.argument.ParsedArgument;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import dev.gurwi.prism.brigadier.BrigadierAdapter;
import dev.gurwi.prism.brigadier.BrigadierTreeBuilder;
import dev.gurwi.prism.brigadier.HierarchyArgumentsResolver;
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
        return argument instanceof BungeecordArgument<?, ?> && ((BungeecordArgument<?, ?>) argument).canUse(source);
    }

    @Override
    public @Nullable ArgumentType<?> nativeType(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (!(argument instanceof BungeecordArgument<?, ?>) || argument instanceof LiteralArgument) {
            return null;
        }

        return ((BungeecordArgument<?, ?>) argument).getArgumentType();
    }

    @Override
    public int tokens(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return 1;
    }

    @Override
    public boolean greedy(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        ArgumentType<?> type = nativeType(argument);

        return type instanceof StringArgumentType
                && ((StringArgumentType) type).getType() == StringArgumentType.StringType.GREEDY_PHRASE;
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
        if (!(argument instanceof BungeecordArgument<?, ?>) || argument instanceof LiteralArgument) {
            return List.of();
        }

        return ((BungeecordArgument<?, ?>) argument).getFixedValues()
                .map(LiteralValues::get)
                .orElse(List.of());
    }

    @Override
    public @Nullable SuggestionProvider<CommandSender> suggestions(@NotNull AbstractArgument<?, ?, ?, ?> argument, @Nullable ArgumentType<?> nativeType) {
        if (!(argument instanceof BungeecordArgument<?, ?>) || argument instanceof LiteralArgument) {
            return null;
        }

        BungeecordArgument<?, ?> bungeecordArgument = (BungeecordArgument<?, ?>) argument;

        List<SuggestionsSupplier<CommandSender, CompletableFuture<Collection<ArgumentSuggestion>>>> suppliers = bungeecordArgument.getCustomSuggestions();

        if (suppliers.isEmpty()) {
            return null;
        }

        boolean nativeProvided = BrigadierAdapter.providesSuggestions(nativeType);

        return (context, suggestionsBuilder) -> {
            String remaining = suggestionsBuilder.getRemaining();
            String lowered = remaining.toLowerCase(Locale.ROOT);

            SuggestionContext<CommandSender> suggestionContext =
                    new SuggestionContext<>(context.getSource(), suggestionsBuilder.getInput(), remaining, nativeProvided);

            List<CompletableFuture<Collection<ArgumentSuggestion>>> parts = new ArrayList<>(suppliers.size());

            for (SuggestionsSupplier<CommandSender, CompletableFuture<Collection<ArgumentSuggestion>>> supplier : suppliers) {
                CompletableFuture<Collection<ArgumentSuggestion>> supplied = supplier.getSuggestions(suggestionContext);

                if (supplied == null) {
                    parts.add(CompletableFuture.completedFuture(Collections.emptyList()));
                } else {
                    parts.add(supplied.exceptionally(error -> Collections.emptyList()));
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

                        suggestionsBuilder.suggest(text);
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

    private @NotNull CommandArguments<BungeecordCommandDefinition, CommandSender> parseHierarchy(@NotNull BungeecordCommandDefinition target, @NotNull CommandContext<CommandSender> context, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) throws CommandSyntaxException {
        return HierarchyArgumentsResolver.resolve(builder, this, target, path, forced, context.getInput(), new HierarchyArgumentsResolver.Reader() {
            @Override
            public boolean present(@NotNull NodeSpec spec) {
                return BungeecordCommandDefinitionAdapter.this.present(context, spec.name());
            }

            @Override
            public @NotNull ParsedArgument<?> read(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull List<NodeSpec> specs) {
                String name = specs.get(0).name();

                return new ParsedArgument<>(HierarchyArgumentsResolver.raw(context, name), BungeecordCommandDefinitionAdapter.this.read(context, name));
            }

            @Override
            public @NotNull ParsedArgument<?> parseFixed(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String value) throws CommandSyntaxException {
                return new ParsedArgument<>(value, BungeecordCommandDefinitionAdapter.this.parseFixed((BungeecordArgument<?, ?>) argument, value));
            }
        });
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