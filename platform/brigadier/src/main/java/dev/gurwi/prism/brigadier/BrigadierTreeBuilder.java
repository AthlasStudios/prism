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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        List<CommandNode<S>> children = new ArrayList<>();

        for (Definition child : definition.getChildren()) {
            for (String childLabel : labels(child)) {
                children.add(build(child, childLabel));
            }
        }

        for (AbstractArgument<?, ?, ?, ?> branch : adapter.branches(definition)) {
            children.add(buildBranch(definition, branch, List.of()));
        }

        requireUnique(definition.getName(), children);

        CommandNode<S> next = null;

        for (int i = slots.size() - 1; i >= 0; i--) {
            Slot slot = slots.get(i);
            ArgumentBuilder<S, ?> builder = node(slot);

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
            addSlots(slots, argument, nodeName(definition, argument), index == requiredSize - 1, false);
            index++;
        }

        for (AbstractArgument<?, ?, ?, ?> argument : definition.getOptionalArguments()) {
            addSlots(slots, argument, nodeName(definition, argument), true, true);
        }

        return slots;
    }

    public @NotNull List<Slot> treeSlots(@NotNull Definition definition, @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> path) {
        List<Slot> slots = new ArrayList<>();

        addSlots(slots, path.get(path.size() - 1), treeNodeName(definition, path), true, false);

        return slots;
    }

    public @NotNull String nodeName(@NotNull Definition definition, @NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (adapter.literal(argument)) {
            return argument.getName();
        }

        return occurrences(definition, argument.getName()) > 1 ? argument.getName() + "_" + depth(definition) : argument.getName();
    }

    private @NotNull String treeNodeName(@NotNull Definition definition, @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> path) {
        AbstractArgument<?, ?, ?, ?> argument = path.get(path.size() - 1);

        if (adapter.literal(argument)) {
            return argument.getName();
        }

        int occurrences = occurrences(definition, argument.getName());

        for (AbstractArgument<?, ?, ?, ?> candidate : path) {
            if (counts(candidate, argument.getName())) {
                occurrences++;
            }
        }

        return occurrences > 1 ? argument.getName() + "_t" + path.size() : argument.getName();
    }

    private @Nullable CommandNode<S> buildBranch(@NotNull Definition definition, @NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull List<AbstractArgument<?, ?, ?, ?>> ancestors) {
        List<AbstractArgument<?, ?, ?, ?>> path = new ArrayList<>(ancestors);
        path.add(argument);

        List<Slot> slots = treeSlots(definition, path);

        if (slots.isEmpty()) {
            throw new IllegalStateException("Argument '" + argument.getName() + "' of '" + definition.getName() + "' consumes no input");
        }

        List<? extends AbstractArgument<?, ?, ?, ?>> childArguments = adapter.children(argument);

        if (slots.get(slots.size() - 1).greedy() && !childArguments.isEmpty()) {
            throw new IllegalStateException("Greedy argument '" + argument.getName() + "' of '" + definition.getName() + "' cannot have children");
        }

        List<CommandNode<S>> children = new ArrayList<>();

        for (AbstractArgument<?, ?, ?, ?> child : childArguments) {
            children.add(buildBranch(definition, child, path));
        }

        requireUnique(argument.getName(), children);

        Command<S> command = adapter.command(definition, List.copyOf(path));
        CommandNode<S> next = null;

        for (int i = slots.size() - 1; i >= 0; i--) {
            Slot slot = slots.get(i);
            ArgumentBuilder<S, ?> builder = node(slot);

            if (slot.terminal()) {
                if (command != null) {
                    builder.executes(command);
                }

                for (CommandNode<S> child : children) {
                    builder.then(child);
                }
            }

            if (next != null) {
                builder.then(next);
            }

            next = builder.build();
        }

        return next;
    }

    private @NotNull ArgumentBuilder<S, ?> node(@NotNull Slot slot) {
        if (slot.literal()) {
            LiteralArgumentBuilder<S> literal = LiteralArgumentBuilder.literal(slot.name());
            literal.requires(source -> adapter.available(slot.argument(), source));

            return literal;
        }

        RequiredArgumentBuilder<S, ?> builder = RequiredArgumentBuilder.argument(slot.name(), slot.type());
        builder.requires(source -> adapter.available(slot.argument(), source));

        SuggestionProvider<S> suggestions = adapter.suggestions(slot.argument(), resolveType(slot.argument()));

        if (suggestions != null) {
            builder.suggests(suggestions);
        }

        return builder;
    }

    private void addSlots(@NotNull List<Slot> slots, @NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String base, boolean terminal, boolean optional) {
        if (adapter.literal(argument)) {
            slots.add(new Slot(argument, base, StringArgumentType.word(), terminal, false, optional, 0, 1, true));
            return;
        }

        boolean greedy = adapter.greedy(argument);
        ArgumentType<?> resolved = resolveType(argument);

        if (resolved != null) {
            slots.add(new Slot(argument, base, resolved, terminal, greedy, optional, 0, 1, false));
            return;
        }

        if (greedy) {
            slots.add(new Slot(argument, base, StringArgumentType.greedyString(), terminal, true, optional, 0, 1, false));
            return;
        }

        int tokens = adapter.tokens(argument);

        for (int part = 0; part < tokens; part++) {
            String name = part == 0 ? base : base + ":" + part;
            slots.add(new Slot(argument, name, StringArgumentType.word(), terminal && part == tokens - 1, false, optional, part, tokens, false));
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

    private void requireUnique(@NotNull String owner, @NotNull List<CommandNode<S>> nodes) {
        Set<String> names = new HashSet<>();

        for (CommandNode<S> node : nodes) {
            if (!names.add(node.getName())) {
                throw new IllegalStateException("Duplicate child '" + node.getName() + "' under '" + owner + "'");
            }
        }
    }

    private int occurrences(@NotNull Definition definition, @NotNull String name) {
        int occurrences = 0;
        Definition current = definition;

        while (current != null) {
            for (AbstractArgument<?, ?, ?, ?> candidate : current.getRequiredArguments()) {
                if (counts(candidate, name)) occurrences++;
            }

            for (AbstractArgument<?, ?, ?, ?> candidate : current.getOptionalArguments()) {
                if (counts(candidate, name)) occurrences++;
            }

            current = current.getParent().orElse(null);
        }

        return occurrences;
    }

    private int depth(@NotNull Definition definition) {
        int depth = 0;
        Definition current = definition.getParent().orElse(null);

        while (current != null) {
            depth++;
            current = current.getParent().orElse(null);
        }

        return depth;
    }

    private boolean counts(@NotNull AbstractArgument<?, ?, ?, ?> candidate, @NotNull String name) {
        return !adapter.literal(candidate) && candidate.getName().equals(name);
    }

    public record Slot(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String name, @NotNull ArgumentType<?> type, boolean terminal, boolean greedy, boolean optional, int part, int parts, boolean literal) {
    }

}