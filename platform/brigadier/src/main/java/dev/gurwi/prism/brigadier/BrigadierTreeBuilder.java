package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BrigadierTreeBuilder<S, Definition extends AbstractCommandDefinition<Definition, ?, ?, ?, ?>> {

    private final BrigadierAdapter<S, Definition> adapter;
    private final Map<AbstractArgument<?, ?, ?, ?>, String> merged = new IdentityHashMap<>();

    public BrigadierTreeBuilder(@NotNull BrigadierAdapter<S, Definition> adapter) {
        this.adapter = adapter;
    }

    public @NotNull @Unmodifiable List<String> labels(@NotNull Definition definition) {
        return Stream.concat(Stream.of(definition.getName()), definition.getAliases().stream())
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    public @NotNull LiteralCommandNode<S> build(@NotNull Definition definition, @NotNull String label) {
        return build(definition, label, Map.of());
    }

    private @NotNull LiteralCommandNode<S> build(@NotNull Definition definition, @NotNull String label, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        List<NodeSpec> nodeSpecs = specs(definition);
        Supplier<List<CommandNode<S>>> children = memo(() -> children(definition, forced));

        LiteralArgumentBuilder<S> literal = LiteralArgumentBuilder.literal(label);
        literal.requires(source -> adapter.visible(definition, source));

        if (definition.getRequiredArguments().isEmpty()) {
            decorate(literal, definition, children.get(), false, forced);
        }

        for (CommandNode<S> node : buildSpecs(definition, nodeSpecs, 0, forced, children)) {
            literal.then(node);
        }

        return literal.build();
    }

    private @NotNull List<CommandNode<S>> children(@NotNull Definition definition, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        List<CommandNode<S>> children = new ArrayList<>();

        for (Definition child : definition.getChildren()) {
            for (String childLabel : labels(child)) {
                children.add(build(child, childLabel, forced));
            }
        }

        children.addAll(buildBranches(definition, adapter.branches(definition), List.of(), forced));

        requireUnique(definition.getName(), children);

        return children;
    }

    private @NotNull List<CommandNode<S>> buildSpecs(@NotNull Definition definition, @NotNull List<NodeSpec> nodeSpecs, int index, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced, @NotNull Supplier<List<CommandNode<S>>> children) {
        if (index >= nodeSpecs.size()) {
            return List.of();
        }

        NodeSpec nodeSpec = nodeSpecs.get(index);
        AbstractArgument<?, ?, ?, ?> argument = nodeSpec.argument();

        if (nodeSpec.literal() || !forcing(argument)) {
            ArgumentBuilder<S, ?> builder = node(nodeSpec);

            finishSpec(builder, definition, nodeSpecs, index, forced, children);

            return List.of(builder.build());
        }

        List<String> values = adapter.forcedValues(argument);

        List<CommandNode<S>> nodes = new ArrayList<>(values.size());

        for (String value : values) {
            Map<AbstractArgument<?, ?, ?, ?>, String> next = new IdentityHashMap<>(forced);
            next.put(argument, value);

            LiteralArgumentBuilder<S> literal = forcedLiteral(argument, value);

            finishSpec(literal, definition, nodeSpecs, index, next, memo(() -> children(definition, next)));

            nodes.add(literal.build());
        }

        return nodes;
    }

    private void finishSpec(@NotNull ArgumentBuilder<S, ?> builder, @NotNull Definition definition, @NotNull List<NodeSpec> nodeSpecs, int index, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced, @NotNull Supplier<List<CommandNode<S>>> children) {
        NodeSpec nodeSpec = nodeSpecs.get(index);
        List<CommandNode<S>> tail = buildSpecs(definition, nodeSpecs, index + 1, forced, children);

        if (nodeSpec.terminal()) {
            List<CommandNode<S>> attached = children.get();
            List<CommandNode<S>> siblings = new ArrayList<>(attached);
            siblings.addAll(tail);

            requireUnique(definition.getName(), siblings);

            decorate(builder, definition, attached, nodeSpec.greedy(), forced);
        }

        for (CommandNode<S> node : tail) {
            builder.then(node);
        }
    }

    private @NotNull LiteralArgumentBuilder<S> forcedLiteral(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String value) {
        LiteralArgumentBuilder<S> literal = LiteralArgumentBuilder.literal(value);
        literal.requires(source -> adapter.available(argument, source));

        return literal;
    }

    private @NotNull Supplier<List<CommandNode<S>>> memo(@NotNull Supplier<List<CommandNode<S>>> source) {
        List<List<CommandNode<S>>> box = new ArrayList<>(1);

        return () -> {
            if (box.isEmpty()) {
                box.add(source.get());
            }

            return box.get(0);
        };
    }

    public @Nullable ArgumentType<?> resolveType(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        ArgumentType<?> override = adapter.nativeType(argument);

        if (override != null) {
            return override;
        }

        return argument instanceof BrigadierCompatibleArgument<?> ? ((BrigadierCompatibleArgument<?>) argument).brigadierType() : null;
    }

    public @NotNull List<NodeSpec> specs(@NotNull Definition definition) {
        List<NodeSpec> nodeSpecs = new ArrayList<>();
        int requiredSize = definition.getRequiredArguments().size();
        int index = 0;

        for (AbstractArgument<?, ?, ?, ?> argument : definition.getRequiredArguments()) {
            addSpecs(nodeSpecs, argument, nodeName(definition, argument), index == requiredSize - 1, false);
            index++;
        }

        for (AbstractArgument<?, ?, ?, ?> argument : definition.getOptionalArguments()) {
            addSpecs(nodeSpecs, argument, nodeName(definition, argument), true, true);
        }

        return nodeSpecs;
    }

    public @NotNull List<NodeSpec> treeSpecs(@NotNull Definition definition, @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> path) {
        List<NodeSpec> nodeSpecs = new ArrayList<>();

        addSpecs(nodeSpecs, path.get(path.size() - 1), treeNodeName(definition, path), true, false);

        return nodeSpecs;
    }

    public @NotNull String nodeName(@NotNull Definition definition, @NotNull AbstractArgument<?, ?, ?, ?> argument) {
        if (adapter.literal(argument)) {
            return argument.getName();
        }

        return occurrences(definition, argument.getName()) > 1 ? argument.getName() + "_" + depth(definition) : argument.getName();
    }

    private @NotNull String treeNodeName(@NotNull Definition definition, @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> path) {
        AbstractArgument<?, ?, ?, ?> argument = path.get(path.size() - 1);
        String override = merged.get(argument);

        if (override != null) {
            return override;
        }

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

    private boolean forcing(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return !adapter.forcedValues(argument).isEmpty()
                && !adapter.literal(argument)
                && !adapter.greedy(argument)
                && adapter.tokens(argument) == 1;
    }

    private boolean mergeable(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return !adapter.literal(argument)
                && !forcing(argument)
                && !adapter.greedy(argument)
                && resolveType(argument) == null
                && adapter.tokens(argument) == 1
                && adapter.children(argument).isEmpty();
    }

    private @NotNull List<CommandNode<S>> buildBranches(@NotNull Definition definition, @NotNull List<? extends AbstractArgument<?, ?, ?, ?>> siblings, @NotNull List<AbstractArgument<?, ?, ?, ?>> ancestors, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        List<AbstractArgument<?, ?, ?, ?>> ordered = new ArrayList<>(siblings);
        ordered.sort(Comparator.comparingInt(argument -> !adapter.literal(argument) && !forcing(argument) && resolveType(argument) == null ? 1 : 0));

        List<AbstractArgument<?, ?, ?, ?>> group = new ArrayList<>();

        for (AbstractArgument<?, ?, ?, ?> sibling : ordered) {
            if (mergeable(sibling)) {
                group.add(sibling);
            }
        }

        boolean merge = group.size() >= 2;
        List<CommandNode<S>> nodes = new ArrayList<>();

        for (AbstractArgument<?, ?, ?, ?> sibling : ordered) {
            if (forcing(sibling)) {
                List<String> values = adapter.forcedValues(sibling);

                for (String value : values) {
                    nodes.add(buildForcedBranch(definition, sibling, value, ancestors, forced));
                }
            } else if (!merge || !group.contains(sibling)) {
                CommandNode<S> node = buildBranch(definition, sibling, ancestors, forced);

                if (node != null) {
                    nodes.add(node);
                }
            }
        }

        if (merge) {
            nodes.add(buildMerged(definition, group, ancestors, forced));
        }

        return nodes;
    }

    private @NotNull CommandNode<S> buildMerged(@NotNull Definition definition, @NotNull List<AbstractArgument<?, ?, ?, ?>> group, @NotNull List<AbstractArgument<?, ?, ?, ?>> ancestors, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        List<String> names = new ArrayList<>();

        for (AbstractArgument<?, ?, ?, ?> argument : group) {
            names.add(argument.getName());
        }

        String name = String.join("|", names);

        for (AbstractArgument<?, ?, ?, ?> argument : group) {
            merged.put(argument, name);
        }

        Map<AbstractArgument<?, ?, ?, ?>, Command<S>> commands = new IdentityHashMap<>();

        for (AbstractArgument<?, ?, ?, ?> argument : group) {
            List<AbstractArgument<?, ?, ?, ?>> path = new ArrayList<>(ancestors);
            path.add(argument);

            Command<S> command = adapter.command(definition, List.copyOf(path), forced);

            if (command != null) {
                commands.put(argument, command);
            }
        }

        RequiredArgumentBuilder<S, String> builder = RequiredArgumentBuilder.argument(name, StringArgumentType.word());
        builder.requires(source -> {
            for (AbstractArgument<?, ?, ?, ?> argument : group) {
                if (adapter.available(argument, source)) {
                    return true;
                }
            }

            return false;
        });

        builder.suggests((context, suggestions) -> {
            for (AbstractArgument<?, ?, ?, ?> argument : group) {
                if (!adapter.available(argument, context.getSource())) {
                    continue;
                }

                SuggestionProvider<S> provider = adapter.suggestions(argument, null);

                if (provider != null) {
                    provider.getSuggestions(context, suggestions);
                }
            }

            return suggestions.buildFuture();
        });

        if (!commands.isEmpty()) {
            builder.executes(context -> {
                String word = context.getArgument(name, String.class);

                for (AbstractArgument<?, ?, ?, ?> argument : group) {
                    Command<S> command = commands.get(argument);

                    if (command != null
                            && adapter.available(argument, context.getSource())
                            && adapter.accepts(argument, context.getSource(), word)) {
                        return command.run(context);
                    }
                }

                throw new SimpleCommandExceptionType(new LiteralMessage("Invalid value: " + word)).create();
            });
        }

        return builder.build();
    }

    private @NotNull CommandNode<S> buildForcedBranch(@NotNull Definition definition, @NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String value, @NotNull List<AbstractArgument<?, ?, ?, ?>> ancestors, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        List<AbstractArgument<?, ?, ?, ?>> path = new ArrayList<>(ancestors);
        path.add(argument);

        Map<AbstractArgument<?, ?, ?, ?>, String> next = new IdentityHashMap<>(forced);
        next.put(argument, value);

        List<CommandNode<S>> children = buildBranches(definition, adapter.children(argument), path, next);

        requireUnique(argument.getName(), children);

        LiteralArgumentBuilder<S> literal = forcedLiteral(argument, value);

        Command<S> command = adapter.command(definition, List.copyOf(path), next);

        if (command != null) {
            literal.executes(command);
        }

        for (CommandNode<S> child : children) {
            literal.then(child);
        }

        return literal.build();
    }

    private @Nullable CommandNode<S> buildBranch(@NotNull Definition definition, @NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull List<AbstractArgument<?, ?, ?, ?>> ancestors, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        List<AbstractArgument<?, ?, ?, ?>> path = new ArrayList<>(ancestors);
        path.add(argument);

        List<NodeSpec> nodeSpecs = treeSpecs(definition, path);

        if (nodeSpecs.isEmpty()) {
            throw new IllegalStateException("Argument '" + argument.getName() + "' of '" + definition.getName() + "' consumes no input");
        }

        List<? extends AbstractArgument<?, ?, ?, ?>> childArguments = adapter.children(argument);

        if (nodeSpecs.get(nodeSpecs.size() - 1).greedy() && !childArguments.isEmpty()) {
            throw new IllegalStateException("Greedy argument '" + argument.getName() + "' of '" + definition.getName() + "' cannot have children");
        }

        List<CommandNode<S>> children = new ArrayList<>(buildBranches(definition, childArguments, path, forced));

        requireUnique(argument.getName(), children);

        Command<S> command = adapter.command(definition, List.copyOf(path), forced);
        CommandNode<S> next = null;

        for (int i = nodeSpecs.size() - 1; i >= 0; i--) {
            NodeSpec nodeSpec = nodeSpecs.get(i);
            ArgumentBuilder<S, ?> builder = node(nodeSpec);

            if (nodeSpec.terminal()) {
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

    private @NotNull ArgumentBuilder<S, ?> node(@NotNull NodeSpec nodeSpec) {
        if (nodeSpec.literal()) {
            LiteralArgumentBuilder<S> literal = LiteralArgumentBuilder.literal(nodeSpec.name());
            literal.requires(source -> adapter.available(nodeSpec.argument(), source));

            return literal;
        }

        RequiredArgumentBuilder<S, ?> builder = RequiredArgumentBuilder.argument(nodeSpec.name(), nodeSpec.type());
        builder.requires(source -> adapter.available(nodeSpec.argument(), source));

        SuggestionProvider<S> suggestions = suggestionsFor(nodeSpec);

        if (suggestions != null) {
            builder.suggests(suggestions);
        }

        return builder;
    }

    private @Nullable SuggestionProvider<S> suggestionsFor(@NotNull NodeSpec nodeSpec) {
        ArgumentType<?> resolved = resolveType(nodeSpec.argument());
        SuggestionProvider<S> user = adapter.suggestions(nodeSpec.argument(), resolved);

        if (user == null) {
            return null;
        }

        if (resolved == null) {
            return user;
        }

        return (context, builder) -> {
            CompletableFuture<Suggestions> nativeSuggestions = resolved.listSuggestions(context, builder.createOffset(builder.getStart()));
            CompletableFuture<Suggestions> userSuggestions = user.getSuggestions(context, builder.createOffset(builder.getStart()));

            return userSuggestions.thenCombine(nativeSuggestions, (first, second) -> Suggestions.merge(builder.getInput(), List.of(first, second)));
        };
    }

    private void addSpecs(@NotNull List<NodeSpec> nodeSpecs, @NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String base, boolean terminal, boolean optional) {
        if (adapter.literal(argument)) {
            nodeSpecs.add(new NodeSpec(argument, base, StringArgumentType.word(), terminal, false, optional, 0, 1, true));
            return;
        }

        boolean greedy = adapter.greedy(argument);
        ArgumentType<?> resolved = resolveType(argument);

        if (resolved != null) {
            nodeSpecs.add(new NodeSpec(argument, base, resolved, terminal, greedy, optional, 0, 1, false));
            return;
        }

        if (greedy) {
            nodeSpecs.add(new NodeSpec(argument, base, StringArgumentType.greedyString(), terminal, true, optional, 0, 1, false));
            return;
        }

        int tokens = adapter.tokens(argument);

        for (int part = 0; part < tokens; part++) {
            String name = part == 0 ? base : base + ":" + part;
            nodeSpecs.add(new NodeSpec(argument, name, StringArgumentType.word(), terminal && part == tokens - 1, false, optional, part, tokens, false));
        }
    }

    private void decorate(@NotNull ArgumentBuilder<S, ?> builder, @NotNull Definition definition, @NotNull List<CommandNode<S>> children, boolean greedy, @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced) {
        Command<S> command = adapter.command(definition, forced);

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

}