package dev.gurwi.prism.brigadier.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class NodeMirror<S> {

    private final CommandDispatcher<MinecraftBrigadierSource> own;
    private final Function<S, MinecraftBrigadierSource> wrapper;

    public NodeMirror(CommandDispatcher<MinecraftBrigadierSource> own, Function<S, MinecraftBrigadierSource> wrapper) {
        this.own = own;
        this.wrapper = wrapper;
    }

    public LiteralCommandNode<S> mirror(LiteralCommandNode<MinecraftBrigadierSource> root, String name) {
        return (LiteralCommandNode<S>) convert(root, root.getName(), name).build();
    }

    private ArgumentBuilder<S, ?> convert(CommandNode<MinecraftBrigadierSource> node, String label, String name) {
        ArgumentBuilder<S, ?> builder;

        if (node instanceof ArgumentCommandNode<MinecraftBrigadierSource, ?> argument) {
            builder = argumentBuilder(argument, label);
        } else {
            builder = LiteralArgumentBuilder.<S>literal(name);
        }

        builder.requires(source -> node.canUse(wrapper.apply(source)));

        if (node.getCommand() != null) {
            builder.executes(context -> own.execute(rewrite(context.getInput(), label), wrapper.apply(context.getSource())));
        }

        for (CommandNode<MinecraftBrigadierSource> child : node.getChildren()) {
            builder.then(convert(child, label, child.getName()));
        }

        return builder;
    }

    private <T> RequiredArgumentBuilder<S, T> argumentBuilder(ArgumentCommandNode<MinecraftBrigadierSource, T> node, String label) {
        RequiredArgumentBuilder<S, T> builder = RequiredArgumentBuilder.<S, T>argument(node.getName(), node.getType());

        if (node.getCustomSuggestions() != null) {
            builder.suggests((context, suggestions) -> suggest(suggestions, context.getSource(), label));
        }

        return builder;
    }

    private CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, S source, String label) {
        String raw = builder.getInput();
        String input = rewrite(raw, label);
        int shift = raw.length() - input.length();

        return own.getCompletionSuggestions(own.parse(input, wrapper.apply(source)))
                .thenApply(result -> shift(result, shift));
    }

    private Suggestions shift(Suggestions suggestions, int shift) {
        if (shift == 0 || suggestions.isEmpty()) {
            return suggestions;
        }

        List<Suggestion> shifted = new ArrayList<>();

        for (Suggestion suggestion : suggestions.getList()) {
            StringRange range = new StringRange(suggestion.getRange().getStart() + shift, suggestion.getRange().getEnd() + shift);
            shifted.add(new Suggestion(range, suggestion.getText(), suggestion.getTooltip()));
        }

        StringRange full = new StringRange(suggestions.getRange().getStart() + shift, suggestions.getRange().getEnd() + shift);

        return new Suggestions(full, shifted);
    }

    private String rewrite(String input, String label) {
        int start = input.startsWith("/") ? 1 : 0;
        int space = input.indexOf(' ', start);

        return label + (space < 0 ? "" : input.substring(space));
    }

}