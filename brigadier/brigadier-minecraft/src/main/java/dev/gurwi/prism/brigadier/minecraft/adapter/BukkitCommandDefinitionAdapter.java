package dev.gurwi.prism.brigadier.minecraft.adapter;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.argument.ParsedArgument;
import dev.gurwi.prism.argument.suggestion.SuggestionContext;
import dev.gurwi.prism.brigadier.BrigadierAdapter;
import dev.gurwi.prism.brigadier.BrigadierCompatibleArgument;
import dev.gurwi.prism.brigadier.BrigadierTreeBuilder;
import dev.gurwi.prism.brigadier.HierarchyArgumentsResolver;
import dev.gurwi.prism.brigadier.NativeTypeHolder;
import dev.gurwi.prism.brigadier.NodeSpec;
import dev.gurwi.prism.brigadier.minecraft.argument.mapping.ArgumentMappings;
import dev.gurwi.prism.brigadier.minecraft.argument.mapping.BoundArgumentMapper;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.base.GreedyArgument;
import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.argument.base.LiteralValues;
import dev.gurwi.prism.bukkit.argument.parser.ArgumentTokens;
import dev.gurwi.prism.bukkit.argument.suggestion.ArgumentSuggestion;
import dev.gurwi.prism.bukkit.argument.suggestion.BukkitSuggestionsSupplier;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.exception.CommandExecutionException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BukkitCommandDefinitionAdapter implements BrigadierAdapter<MinecraftCommandSource, BukkitCommandDefinition> {

    private static final SimpleCommandExceptionType NO_EXECUTOR = new SimpleCommandExceptionType(new LiteralMessage("This command cannot be run by this sender"));

    private final BrigadierTreeBuilder<MinecraftCommandSource, BukkitCommandDefinition> builder = new BrigadierTreeBuilder<>(this);

    @Override
    public boolean visible(@NotNull BukkitCommandDefinition definition, @NotNull MinecraftCommandSource source) {
        return visible(definition, senderOf(source));
    }

    @Override
    public boolean available(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull MinecraftCommandSource source) {
        if (argument instanceof BukkitArgument<?, ?> bukkitArgument) {

            return bukkitArgument.getRequirement().test(senderOf(source));
        }

        return false;
    }

    @Override
    public @NotNull List<String> forcedValues(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (!(argument instanceof BukkitArgument<?, ?> bukkitArgument) || argument instanceof LiteralArgument) {
            return List.of();
        }

        List<String> values = bukkitArgument.getFixedValues()
                .map(LiteralValues::get)
                .orElse(List.of());

        if (values.isEmpty()) {
            return values;
        }

        ArgumentType<?> resolved = builder.resolveType(argument);

        if (bukkitArgument.isFixedValuesUnlessNative() && resolved != null) {
            return List.of();
        }

        if (bukkitArgument.getSuggestionsSupplier().isPresent()) {
            return List.of();
        }

        return values;
    }

    @Override
    public boolean accepts(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull MinecraftCommandSource source, @NotNull String word) {
        if (!(argument instanceof BukkitArgument<?, ?> bukkitArgument)) {
            return false;
        }

        try {
            bukkitArgument.parse(senderOf(source), word);
            return true;
        } catch (ArgumentParseException e) {
            return false;
        }
    }

    @Override
    public @Nullable ArgumentType<?> nativeType(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (!(argument instanceof BukkitArgument<?, ?> bukkitArgument)) {
            return null;
        }

        return ArgumentMappings.INSTANCE.find(bukkitArgument)
                .map(BoundArgumentMapper::type)
                .orElse(null);
    }

    @Override
    public int tokens(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (!(argument instanceof BukkitArgument<?, ?>)) {
            return 0;
        }

        return ArgumentTokens.consumption((BukkitArgument<?, ?>) argument);
    }

    @Override
    public boolean greedy(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return argument instanceof GreedyArgument<?, ?>;
    }

    @Override
    public boolean literal(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return argument instanceof LiteralArgument;
    }

    @Override
    public @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> branches(@NotNull BukkitCommandDefinition definition) {
        return arguments(definition.getArgumentTreeBranches());
    }

    @Override
    public @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> children(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return arguments(((BukkitArgument<?, ?>) argument).getArguments());
    }

    @Override
    public @Nullable Command<MinecraftCommandSource> command(@NotNull BukkitCommandDefinition definition) {
        return command(definition, Map.of());
    }

    @Override
    public @Nullable Command<MinecraftCommandSource> command(@NotNull BukkitCommandDefinition definition, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        return ctx -> execute(definition, forced, ctx);
    }

    @Override
    public @Nullable Command<MinecraftCommandSource> command(@NotNull BukkitCommandDefinition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path) {
        return command(definition, path, Map.of());
    }

    @Override
    public @Nullable Command<MinecraftCommandSource> command(@NotNull BukkitCommandDefinition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        BukkitArgument<?, ?> last = (BukkitArgument<?, ?>) path.get(path.size() - 1);

        if (last.getExecutors().isEmpty()) {
            return null;
        }

        return ctx -> executeNode(definition, path, forced, ctx);
    }

    @Override
    public @Nullable SuggestionProvider<MinecraftCommandSource> suggestions(@NotNull AbstractArgument<?, ?, ?, ?> argument, @Nullable ArgumentType<?> nativeType) {
        BukkitArgument<?, ?> bukkitArgument = (BukkitArgument<?, ?>) argument;

        Optional<BukkitSuggestionsSupplier<?>> replaced = bukkitArgument.getSuggestionsSupplier();

        boolean nativeProvided = BrigadierAdapter.providesSuggestions(nativeType);
        ArgumentType<?> nativeSuggester = nativeType instanceof NativeTypeHolder ? ((NativeTypeHolder) nativeType).getNativeType() : nativeType;

        List<BukkitSuggestionsSupplier<?>> suppliers = new ArrayList<>();

        for (BukkitSuggestionsSupplier<?> included : bukkitArgument.getIncludedSuggestions()) {
            if (nativeProvided && included instanceof BukkitSuggestionsSupplier.UnlessNative) {
                continue;
            }

            suppliers.add(included);
        }

        if (suppliers.isEmpty() && replaced.isEmpty()) {
            return null;
        }

        return (context, suggestionsBuilder) -> {
            CommandSender sender = senderOf(context.getSource());
            String remaining = suggestionsBuilder.getRemaining();

            SuggestionContext<CommandSender> suggestionContext =
                    new SuggestionContext<>(sender, suggestionsBuilder.getInput(), remaining, nativeProvided);

            CompletableFuture<Collection<ArgumentSuggestion>> replacedPart = replaced
                    .map(supplier -> BukkitSuggestionsSupplier.resolveRaw(supplier, suggestionContext))
                    .orElse(null);

            List<CompletableFuture<Collection<ArgumentSuggestion>>> parts = new ArrayList<>(suppliers.size() + 1);

            if (replacedPart != null) {
                parts.add(replacedPart);
            }

            for (BukkitSuggestionsSupplier<?> supplier : suppliers) {
                parts.add(BukkitSuggestionsSupplier.resolve(supplier, suggestionContext));
            }

            return CompletableFuture.allOf(parts.toArray(new CompletableFuture[0])).handle((ignored, error) -> {
                Set<String> seen = new HashSet<>();
                boolean deferred = replacedPart != null && nativeType != null && !replacedPart.isCompletedExceptionally() && replacedPart.join() == null;

                for (CompletableFuture<Collection<ArgumentSuggestion>> part : parts) {
                    if (part.isCompletedExceptionally()) continue;

                    Collection<ArgumentSuggestion> suggestions = part.join();

                    if (suggestions != null) {
                        applySuggestions(suggestionsBuilder, suggestions, remaining, seen);
                    }
                }

                if (deferred) {
                    return nativeSuggester.listSuggestions(context, suggestionsBuilder);
                }

                return CompletableFuture.completedFuture(suggestionsBuilder.build());
            }).thenCompose(future -> future);
        };
    }

    private static void applySuggestions(@NotNull SuggestionsBuilder builder, @NotNull Collection<ArgumentSuggestion> suggestions, @NotNull String remaining, @NotNull Set<String> seen) {
        String lowered = remaining.toLowerCase(Locale.ROOT);

        for (ArgumentSuggestion suggestion : suggestions) {
            String text = suggestion.getSuggestion();

            if (!text.toLowerCase(Locale.ROOT).startsWith(lowered) || !seen.add(text)) {
                continue;
            }

            suggestion.getTooltip().ifPresentOrElse(
                    tooltip -> builder.suggest(text, new LiteralMessage(tooltip)),
                    () -> builder.suggest(text)
            );
        }
    }

    public BrigadierTreeBuilder<MinecraftCommandSource, BukkitCommandDefinition> getBuilder() {
        return builder;
    }

    private int execute(@NotNull BukkitCommandDefinition target, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced, @NotNull CommandContext<MinecraftCommandSource> context) throws CommandSyntaxException {
        MinecraftCommandSource source = context.getSource();
        CommandSender sender = senderOf(source);

        if (!target.hasExecutor(sender)) {
            throw NO_EXECUTOR.create();
        }

        CommandArguments<BukkitCommandDefinition, CommandSender> parsed = parseHierarchy(target, context, source, sender, List.of(), forced);

        try {
            target.getExecutors().forEach(executor -> executor.tryInvoke(sender, parsed));
        } catch (CommandExecutionException e) {
            handleCommandExecutionException(sender, e);
        }

        return Command.SINGLE_SUCCESS;
    }

    private int executeNode(@NotNull BukkitCommandDefinition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced, @NotNull CommandContext<MinecraftCommandSource> context) throws CommandSyntaxException {
        MinecraftCommandSource source = context.getSource();
        CommandSender sender = senderOf(source);
        BukkitArgument<?, ?> last = (BukkitArgument<?, ?>) path.get(path.size() - 1);

        if (!last.hasExecutor(sender)) {
            throw NO_EXECUTOR.create();
        }

        CommandArguments<BukkitCommandDefinition, CommandSender> parsed = parseHierarchy(definition, context, source, sender, path, forced);

        try {
            last.getExecutors().forEach(executor -> executor.tryInvoke(sender, parsed));
        } catch (CommandExecutionException e) {
            handleCommandExecutionException(sender, e);
        }

        return Command.SINGLE_SUCCESS;
    }

    private void handleCommandExecutionException(@NotNull CommandSender sender, @NotNull CommandExecutionException e) {
        if (e instanceof CommandExecutionException.Silent) {
            e.printStackTrace();
            return;
        }

        if (e instanceof CommandExecutionException.Message) {
            sender.sendMessage(e.getMessage());
            e.printStackTrace();
            return;
        }

        throw e;
    }

    private @NotNull CommandArguments<BukkitCommandDefinition, CommandSender> parseHierarchy(@NotNull BukkitCommandDefinition target, @NotNull CommandContext<MinecraftCommandSource> context, @NotNull MinecraftCommandSource source, @NotNull CommandSender sender, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) throws CommandSyntaxException {
        return HierarchyArgumentsResolver.resolve(builder, this, target, path, forced, context.getInput(), new HierarchyArgumentsResolver.Reader() {
            @Override
            public boolean present(@NotNull NodeSpec spec) {
                return BukkitCommandDefinitionAdapter.this.present(context, spec.name());
            }

            @Override
            public @NotNull ParsedArgument<?> read(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull List<NodeSpec> specs) throws CommandSyntaxException {
                Object value = BukkitCommandDefinitionAdapter.this.read((BukkitArgument<?, ?>) argument, specs, context, source, sender);

                return new ParsedArgument<>(HierarchyArgumentsResolver.raw(context, specs), value);
            }

            @Override
            public @NotNull ParsedArgument<?> parseFixed(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String value) throws CommandSyntaxException {
                Object parsed = BukkitCommandDefinitionAdapter.this.parseFixed((BukkitArgument<?, ?>) argument, sender, value);

                return new ParsedArgument<>(value, parsed);
            }
        });
    }

    private <T> @NotNull T parseFixed(@NotNull BukkitArgument<?, T> argument, @NotNull CommandSender sender, @NotNull String value) throws CommandSyntaxException {
        try {
            return argument.parse(sender, value);
        } catch (ArgumentParseException e) {
            throw new SimpleCommandExceptionType(new LiteralMessage(String.valueOf(e.getMessage()))).create();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> @NotNull T read(@NotNull BukkitArgument<?, T> argument, @NotNull List<NodeSpec> specs, @NotNull CommandContext<MinecraftCommandSource> context, @NotNull MinecraftCommandSource source, @NotNull CommandSender sender) throws CommandSyntaxException {
        String name = specs.get(0).name();
        Optional<BoundArgumentMapper<T>> mapping = ArgumentMappings.INSTANCE.find(argument);

        if (mapping.isPresent()) {
            return mapping.get().read(context, name, source);
        }

        if (argument instanceof BrigadierCompatibleArgument<?> compatible) {

            return (T) Objects.requireNonNull(compatible.fromBrigadier(context, name));
        }

        StringBuilder joined = new StringBuilder();

        for (NodeSpec spec : specs) {
            if (!joined.isEmpty()) {
                joined.append(' ');
            }

            joined.append(context.getArgument(spec.name(), String.class));
        }

        try {
            return argument.parse(sender, joined.toString());
        } catch (ArgumentParseException e) {
            throw new SimpleCommandExceptionType(new LiteralMessage(String.valueOf(e.getMessage()))).create();
        }
    }

    private boolean present(@NotNull CommandContext<MinecraftCommandSource> context, @NotNull String node) {
        try {
            context.getArgument(node, Object.class);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean visible(@NotNull BukkitCommandDefinition definition, @NotNull CommandSender sender) {
        if (!definition.canExecute(sender)) {
            return false;
        }

        if (definition.hasExecutor(sender)) {
            return true;
        }

        for (AbstractArgument<?, ?, ?, ?> branch : branches(definition)) {
            if (usable((BukkitArgument<?, ?>) branch, sender)) {
                return true;
            }
        }

        return definition.getChildren().stream().anyMatch(child -> visible(child, sender));
    }

    private boolean usable(@NotNull BukkitArgument<?, ?> node, @NotNull CommandSender sender) {
        if (!node.canUse(sender)) {
            return false;
        }

        if (node.hasExecutor(sender)) {
            return true;
        }

        for (AbstractArgument<?, ?, ?, ?> child : children(node)) {
            if (usable((BukkitArgument<?, ?>) child, sender)) {
                return true;
            }
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

    private @NotNull CommandSender senderOf(@NotNull MinecraftCommandSource source) {
        return source.getEntity().map(CommandSender.class::cast).orElse(source.getSender());
    }

}