package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

public final class BrigadierTreeBuilder<S, Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>> {

    private final BrigadierAdapter<S, Definition> adapter;

    public BrigadierTreeBuilder(@NotNull BrigadierAdapter<S, Definition> adapter) {
        this.adapter = adapter;
    }

    public @NotNull @Unmodifiable List<String> labels(@NotNull Definition definition) {
        return Stream.concat(Stream.of(definition.getName()), definition.getAliases().stream()).distinct().toList();
    }

    public @NotNull LiteralCommandNode<S> build(@NotNull Definition definition, @NotNull String label) {
        List<Slot> slots = slots(definition);

        List<CommandNode<S>> children = definition.getChildren().stream()
                .flatMap(child -> labels(child).stream().map(childLabel -> (CommandNode<S>) build(child, childLabel)))
                .toList();

        CommandNode<S> next = null;

        for (int i = slots.size() - 1; i >= 0; i--) {
            Slot slot = slots.get(i);
            RequiredArgumentBuilder<S, ?> builder = RequiredArgumentBuilder.argument(slot.name(), slot.type());
            builder.requires(source -> adapter.available(slot.argument(), source));

            SuggestionProvider<S> suggestions = adapter.suggestions(slot.argument(), resolveType(slot.argument()));

            if (suggestions != null) {
                builder.suggests(suggestions);
            }

            if (slot.terminal()) {
                decorate(builder, definition, children, slot.greedy());
            }

            if (next != null) {
                builder.then(next);
            }

            next = builder.build();
        }

        LiteralArgumentBuilder<S> literal = LiteralArgumentBuilder.literal(label);
        literal.requires(source -> adapter.visible(definition, source));

        if (definition.getRequiredArguments().isEmpty()) {
            decorate(literal, definition, children, false);
        }

        if (next != null) {
            literal.then(next);
        }

        return literal.build();
    }

    public @Nullable ArgumentType<?> resolveType(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        ArgumentType<?> override = adapter.nativeType(argument);

        if (override != null) {
            return override;
        }

        return argument instanceof BrigadierCompatibleArgument<?> compatible ? compatible.brigadierType() : null;
    }

    public @NotNull List<Slot> slots(@NotNull Definition definition) {
        List<Slot> slots = new ArrayList<>();
        int requiredSize = definition.getRequiredArguments().size();
        int index = 0;

        for (AbstractArgument<?, ?, ?, ?> argument : definition.getRequiredArguments()) {
            addSlots(slots, definition, argument, index == requiredSize - 1, false);
            index++;
        }

        for (AbstractArgument<?, ?, ?, ?> argument : definition.getOptionalArguments()) {
            addSlots(slots, definition, argument, true, true);
        }

        return slots;
    }

    public @NotNull String nodeName(@NotNull Definition definition, @NotNull AbstractArgument<?, ?, ?, ?> argument) {
        Deque<Definition> hierarchy = new ArrayDeque<>();
        Definition current = definition;

        while (current != null) {
            hierarchy.addFirst(current);
            current = current.getParent().orElse(null);
        }

        int occurrences = 0;

        for (Definition level : hierarchy) {
            for (AbstractArgument<?, ?, ?, ?> candidate : level.getRequiredArguments()) {
                if (candidate.getName().equals(argument.getName())) occurrences++;
            }
            for (AbstractArgument<?, ?, ?, ?> candidate : level.getOptionalArguments()) {
                if (candidate.getName().equals(argument.getName())) occurrences++;
            }
        }

        return occurrences > 1 ? argument.getName() + "_" + (hierarchy.size() - 1) : argument.getName();
    }

    private void addSlots(@NotNull List<Slot> slots, @NotNull Definition definition, @NotNull AbstractArgument<?, ?, ?, ?> argument, boolean terminal, boolean optional) {
        String base = nodeName(definition, argument);
        boolean greedy = adapter.greedy(argument);
        ArgumentType<?> resolved = resolveType(argument);

        if (resolved != null) {
            slots.add(new Slot(argument, base, resolved, terminal, greedy, optional, 0, 1));
            return;
        }

        if (greedy) {
            slots.add(new Slot(argument, base, StringArgumentType.greedyString(), terminal, true, optional, 0, 1));
            return;
        }

        int tokens = adapter.tokens(argument);

        for (int part = 0; part < tokens; part++) {
            String name = part == 0 ? base : base + ":" + part;
            slots.add(new Slot(argument, name, StringArgumentType.word(), terminal && part == tokens - 1, false, optional, part, tokens));
        }
    }

    private void decorate(@NotNull ArgumentBuilder<S, ?> builder, @NotNull Definition definition, @NotNull List<CommandNode<S>> children, boolean greedy) {
        Command<S> command = adapter.command(definition);

        if (command != null) {
            builder.executes(command);
        }

        if (!greedy) {
            for (CommandNode<S> child : children) {
                builder.then(child);
            }
        }
    }

    public record Slot(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String name, @NotNull ArgumentType<?> type, boolean terminal, boolean greedy, boolean optional, int part, int parts) {
    }

}