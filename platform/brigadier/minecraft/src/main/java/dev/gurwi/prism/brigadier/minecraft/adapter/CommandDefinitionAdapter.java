package dev.gurwi.prism.brigadier.minecraft.adapter;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.brigadier.BrigadierAdapter;
import dev.gurwi.prism.brigadier.BrigadierCompatibleArgument;
import dev.gurwi.prism.brigadier.BrigadierTreeBuilder;
import dev.gurwi.prism.brigadier.minecraft.argument.mapping.ArgumentMappings;
import dev.gurwi.prism.brigadier.minecraft.argument.mapping.BoundArgumentMapper;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.GreedyArgument;
import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.argument.parser.ArgumentTokens;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandDefinitionAdapter implements BrigadierAdapter<MinecraftCommandSource, BukkitCommandDefinition> {

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
    public boolean forced(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return argument instanceof BukkitArgument<?, ?> bukkitArgument
                && !(argument instanceof LiteralArgument)
                && bukkitArgument.isForceSuggestions();
    }

    @Override
    public @NotNull List<String> forcedValues(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (!(argument instanceof BukkitArgument<?, ?> bukkitArgument)) {
            return List.of();
        }

        Set<String> values = new LinkedHashSet<>();

        for (String suggestion : bukkitArgument.getSuggestionsSupplier().getSuggestions(Bukkit.getConsoleSender(), "")) {
            if (suggestion != null && !suggestion.isEmpty() && suggestion.indexOf(' ') < 0) {
                values.add(suggestion);
            }
        }

        return List.copyOf(values);
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
        if (!(argument instanceof BukkitArgument<?, ?> bukkitArgument)) {
            return 0;
        }

        return ArgumentTokens.consumption(bukkitArgument);
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
        return ctx -> execute(definition, ctx);
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
        if (nativeType != null) {
            return null;
        }

        BukkitArgument<?, ?> bukkitArgument = (BukkitArgument<?, ?>) argument;

        return (context, suggestionsBuilder) -> {
            CommandSender sender = senderOf(context.getSource());
            String remaining = suggestionsBuilder.getRemaining().toLowerCase();

            for (String suggestion : bukkitArgument.getSuggestionsSupplier().getSuggestions(sender, suggestionsBuilder.getRemaining())) {
                if (suggestion.toLowerCase().startsWith(remaining)) {
                    suggestionsBuilder.suggest(suggestion);
                }
            }

            return suggestionsBuilder.buildFuture();
        };
    }

    public BrigadierTreeBuilder<MinecraftCommandSource, BukkitCommandDefinition> getBuilder() {
        return builder;
    }

    private int execute(@NotNull BukkitCommandDefinition target, @NotNull CommandContext<MinecraftCommandSource> context) throws CommandSyntaxException {
        MinecraftCommandSource source = context.getSource();
        CommandSender sender = senderOf(source);

        if (!target.hasExecutor(sender)) {
            throw NO_EXECUTOR.create();
        }

        CommandArguments<BukkitCommandDefinition, CommandSender> parsed = parseHierarchy(target, context, source, sender, List.of(), Map.of());

        target.getExecutors().forEach(executor -> executor.tryInvoke(sender, parsed));

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

        last.getExecutors().forEach(executor -> executor.tryInvoke(sender, parsed));

        return Command.SINGLE_SUCCESS;
    }

    private @Nullable CommandArguments<BukkitCommandDefinition, CommandSender> parseHierarchy(@NotNull BukkitCommandDefinition target, @NotNull CommandContext<MinecraftCommandSource> context, @NotNull MinecraftCommandSource source, @NotNull CommandSender sender, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) throws CommandSyntaxException {
        Deque<BukkitCommandDefinition> hierarchy = new ArrayDeque<>();
        BukkitCommandDefinition current = target;

        while (current != null) {
            hierarchy.addFirst(current);
            current = current.getParent().orElse(null);
        }

        CommandArguments<BukkitCommandDefinition, CommandSender> parsed = null;

        for (BukkitCommandDefinition level : hierarchy) {
            parsed = parseLevel(level, context, source, sender, parsed, level == target ? path : List.of(), forced);
        }

        return parsed;
    }

    private @NotNull CommandArguments<BukkitCommandDefinition, CommandSender> parseLevel(@NotNull BukkitCommandDefinition level, @NotNull CommandContext<MinecraftCommandSource> context, @NotNull MinecraftCommandSource source, @NotNull CommandSender sender, CommandArguments<BukkitCommandDefinition, CommandSender> parent, @NotNull List<AbstractArgument<?, ?, ?, ?>> path, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) throws CommandSyntaxException {
        Map<AbstractArgument<?, ?, ?, ?>, List<BrigadierTreeBuilder.Slot>> grouped = new LinkedHashMap<>();

        for (BrigadierTreeBuilder.Slot slot : builder.slots(level)) {
            grouped.computeIfAbsent(slot.argument(), key -> new ArrayList<>()).add(slot);
        }

        Map<String, Object> required = new HashMap<>();
        Map<String, Object> optional = new HashMap<>();

        for (Map.Entry<AbstractArgument<?, ?, ?, ?>, List<BrigadierTreeBuilder.Slot>> entry : grouped.entrySet()) {
            BukkitArgument<?, ?> argument = (BukkitArgument<?, ?>) entry.getKey();
            List<BrigadierTreeBuilder.Slot> slots = entry.getValue();

            if (slots.get(0).literal() || !argument.isListed()) {
                continue;
            }

            boolean isOptional = slots.get(0).optional();

            if (isOptional && !present(context, slots.get(0).name())) {
                continue;
            }

            Object value = read(argument, slots, context, source, sender);

            if (isOptional) {
                optional.put(argument.getName(), value);
            } else {
                required.put(argument.getName(), value);
            }
        }

        for (int index = 0; index < path.size(); index++) {
            BukkitArgument<?, ?> argument = (BukkitArgument<?, ?>) path.get(index);

            if (literal(argument) || !argument.isListed()) {
                continue;
            }

            String fixed = forced.get(argument);

            if (fixed != null) {
                try {
                    required.put(argument.getName(), argument.parse(sender, fixed));
                } catch (ArgumentParseException e) {
                    throw new SimpleCommandExceptionType(new LiteralMessage(String.valueOf(e.getMessage()))).create();
                }

                continue;
            }

            List<BrigadierTreeBuilder.Slot> slots = builder.treeSlots(level, path.subList(0, index + 1));

            required.put(argument.getName(), read(argument, slots, context, source, sender));
        }

        return new CommandArguments<>(level, required, optional, parent);
    }

    @SuppressWarnings("unchecked")
    private <T> @NotNull T read(@NotNull BukkitArgument<?, T> argument, @NotNull List<BrigadierTreeBuilder.Slot> slots, @NotNull CommandContext<MinecraftCommandSource> context, @NotNull MinecraftCommandSource source, @NotNull CommandSender sender) throws CommandSyntaxException {
        String name = slots.get(0).name();
        Optional<BoundArgumentMapper<T>> mapping = ArgumentMappings.INSTANCE.find(argument);

        if (mapping.isPresent()) {
            return mapping.get().read(context, name, source);
        }

        if (argument instanceof BrigadierCompatibleArgument<?> compatible) {
            return (T) Objects.requireNonNull(compatible.fromBrigadier(context, name));
        }

        StringBuilder joined = new StringBuilder();

        for (BrigadierTreeBuilder.Slot slot : slots) {
            if (!joined.isEmpty()) {
                joined.append(' ');
            }

            joined.append(context.getArgument(slot.name(), String.class));
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