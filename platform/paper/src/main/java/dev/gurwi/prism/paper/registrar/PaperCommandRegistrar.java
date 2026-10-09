package dev.gurwi.prism.paper.registrar;

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
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.GreedyArgument;
import dev.gurwi.prism.bukkit.argument.parser.ArgumentTokens;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.paper.mapping.PaperArgumentMapping;
import dev.gurwi.prism.paper.mapping.PaperDefaultMappings;
import dev.gurwi.prism.paper.mapping.PaperMappings;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PaperCommandRegistrar implements BukkitCommandRegistrar, BrigadierAdapter<CommandSourceStack, BukkitCommandDefinition> {

    private static final SimpleCommandExceptionType NO_EXECUTOR = new SimpleCommandExceptionType(new LiteralMessage("This command cannot be run by this sender"));

    private final Set<BukkitCommandDefinition> definitions = ConcurrentHashMap.newKeySet();
    private final BrigadierTreeBuilder<CommandSourceStack, BukkitCommandDefinition> builder = new BrigadierTreeBuilder<>(this);

    public PaperCommandRegistrar(@NotNull JavaPlugin plugin) {
        PaperDefaultMappings.registerDefaults();

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();

            for (BukkitCommandDefinition definition : definitions) {
                for (String label : builder.labels(definition)) {
                    registrar.register(builder.build(definition, label), definition.getDescription(), List.of());
                }
            }
        });
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        definitions.add(definition);
    }

    @Override
    public void unregister(BukkitCommandDefinition definition) {
        definitions.remove(definition);
    }

    @Override
    public @Unmodifiable Set<BukkitCommandDefinition> getRegisteredCommands() {
        return Set.copyOf(definitions);
    }

    @Override
    public Type getType() {
        return Type.PAPER_BRIGADIER;
    }

    @Override
    public boolean visible(@NotNull BukkitCommandDefinition definition, @NotNull CommandSourceStack source) {
        return visible(definition, senderOf(source));
    }

    @Override
    public boolean available(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull CommandSourceStack source) {
        return ((BukkitArgument<?, ?>) argument).getRequirement().test(senderOf(source));
    }

    @Override
    public @Nullable ArgumentType<?> nativeType(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        PaperArgumentMapping<BukkitArgument<?, ?>> mapping = PaperMappings.find(argument);
        return mapping == null ? null : mapping.type((BukkitArgument<?, ?>) argument);
    }

    @Override
    public int tokens(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return ArgumentTokens.consumption((BukkitArgument<?, ?>) argument);
    }

    @Override
    public boolean greedy(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return argument instanceof GreedyArgument<?, ?>;
    }

    @Override
    public @NotNull Command<CommandSourceStack> command(@NotNull BukkitCommandDefinition definition) {
        return context -> execute(definition, context);
    }

    @Override
    public @Nullable SuggestionProvider<CommandSourceStack> suggestions(@NotNull AbstractArgument<?, ?, ?, ?> argument, @Nullable ArgumentType<?> nativeType) {
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

    private int execute(@NotNull BukkitCommandDefinition target, @NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        CommandSender sender = senderOf(source);

        if (!target.hasExecutor(sender)) {
            throw NO_EXECUTOR.create();
        }

        Deque<BukkitCommandDefinition> hierarchy = new ArrayDeque<>();
        BukkitCommandDefinition current = target;

        while (current != null) {
            hierarchy.addFirst(current);
            current = current.getParent().orElse(null);
        }

        CommandArguments<BukkitCommandDefinition, CommandSender> parsed = null;

        for (BukkitCommandDefinition level : hierarchy) {
            parsed = parseLevel(level, context, source, sender, parsed);
        }

        CommandArguments<BukkitCommandDefinition, CommandSender> finalArguments = parsed;

        target.getExecutors().forEach(executor -> executor.tryInvoke(sender, finalArguments));

        return Command.SINGLE_SUCCESS;
    }

    private @NotNull CommandArguments<BukkitCommandDefinition, CommandSender> parseLevel(@NotNull BukkitCommandDefinition level, @NotNull CommandContext<CommandSourceStack> context, @NotNull CommandSourceStack source, @NotNull CommandSender sender, CommandArguments<BukkitCommandDefinition, CommandSender> parent) throws CommandSyntaxException {
        Map<AbstractArgument<?, ?, ?, ?>, List<BrigadierTreeBuilder.Slot>> grouped = new LinkedHashMap<>();

        for (BrigadierTreeBuilder.Slot slot : builder.slots(level)) {
            grouped.computeIfAbsent(slot.argument(), key -> new java.util.ArrayList<>()).add(slot);
        }

        Map<String, Object> required = new HashMap<>();
        Map<String, Object> optional = new HashMap<>();

        for (Map.Entry<AbstractArgument<?, ?, ?, ?>, List<BrigadierTreeBuilder.Slot>> entry : grouped.entrySet()) {
            BukkitArgument<?, ?> argument = (BukkitArgument<?, ?>) entry.getKey();
            List<BrigadierTreeBuilder.Slot> slots = entry.getValue();
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

        return new CommandArguments<>(level, required, optional, parent);
    }

    private @NotNull Object read(@NotNull BukkitArgument<?, ?> argument, @NotNull List<BrigadierTreeBuilder.Slot> slots, @NotNull CommandContext<CommandSourceStack> context, @NotNull CommandSourceStack source, @NotNull CommandSender sender) throws CommandSyntaxException {
        PaperArgumentMapping<BukkitArgument<?, ?>> mapping = PaperMappings.find(argument);

        if (mapping != null) {
            return mapping.convert(argument, source, context.getArgument(slots.getFirst().name(), Object.class));
        }

        if (argument instanceof BrigadierCompatibleArgument<?> compatible) {
            return Objects.requireNonNull(compatible.fromBrigadier(context, slots.getFirst().name()));
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

    private boolean present(@NotNull CommandContext<CommandSourceStack> context, @NotNull String node) {
        try {
            context.getArgument(node, Object.class);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean visible(@NotNull BukkitCommandDefinition definition, @NotNull CommandSender sender) {
        boolean usable = definition.canExecute(sender);

        if (!usable) {
            return false;
        }

        return definition.hasExecutor(sender) || definition.getChildren().stream().anyMatch(child -> visible(child, sender));
    }

    private @NotNull CommandSender senderOf(@NotNull CommandSourceStack source) {
        return source.getExecutor() != null ? source.getExecutor() : source.getSender();
    }

}
