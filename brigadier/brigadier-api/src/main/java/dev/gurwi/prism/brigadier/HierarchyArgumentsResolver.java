package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.argument.ParsedArgument;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HierarchyArgumentsResolver {

    private HierarchyArgumentsResolver() {
    }

    public interface Reader {

        boolean present(@NotNull NodeSpec spec);

        @NotNull ParsedArgument<?> read(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull List<NodeSpec> specs) throws CommandSyntaxException;

        @NotNull ParsedArgument<?> parseFixed(@NotNull AbstractArgument<?, ?, ?, ?> argument, @NotNull String value) throws CommandSyntaxException;

    }

    public static <B, S, D extends AbstractCommandDefinition<D, ? super S, ?, ?, ?>> @NotNull CommandArguments<D, S> resolve(
            @NotNull BrigadierTreeBuilder<B, D> builder,
            @NotNull BrigadierAdapter<B, D> adapter,
            @NotNull D target,
            @NotNull List<AbstractArgument<?, ?, ?, ?>> path,
            @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced,
            @NotNull String fullInput,
            @NotNull Reader reader
    ) throws CommandSyntaxException {
        Deque<D> hierarchy = new ArrayDeque<>();
        D current = target;

        while (current != null) {
            hierarchy.addFirst(current);
            current = current.getParent().orElse(null);
        }

        CommandArguments<D, S> parsed = null;

        for (D level : hierarchy) {
            parsed = resolveLevel(builder, adapter, level, level == target ? path : List.of(), forced, parsed, fullInput, reader);
        }

        return Optional.ofNullable(parsed).orElseThrow();
    }

    private static <B, S, D extends AbstractCommandDefinition<D, ? super S, ?, ?, ?>> @NotNull CommandArguments<D, S> resolveLevel(
            @NotNull BrigadierTreeBuilder<B, D> builder,
            @NotNull BrigadierAdapter<B, D> adapter,
            @NotNull D level,
            @NotNull List<AbstractArgument<?, ?, ?, ?>> path,
            @NotNull Map<AbstractArgument<?, ?, ?, ?>, String> forced,
            @Nullable CommandArguments<D, S> parent,
            @NotNull String fullInput,
            @NotNull Reader reader
    ) throws CommandSyntaxException {
        Map<AbstractArgument<?, ?, ?, ?>, List<NodeSpec>> grouped = new LinkedHashMap<>();

        for (NodeSpec spec : builder.specs(level)) {
            grouped.computeIfAbsent(spec.argument(), key -> new ArrayList<>()).add(spec);
        }

        Map<String, ParsedArgument<?>> required = new HashMap<>();
        Map<String, ParsedArgument<?>> optional = new HashMap<>();

        for (Map.Entry<AbstractArgument<?, ?, ?, ?>, List<NodeSpec>> entry : grouped.entrySet()) {
            AbstractArgument<?, ?, ?, ?> argument = entry.getKey();
            List<NodeSpec> specs = entry.getValue();
            NodeSpec first = specs.get(0);

            if (first.literal() || !argument.isListed()) {
                continue;
            }

            String fixed = forced.get(argument);

            if (fixed == null && first.optional() && !reader.present(first)) {
                continue;
            }

            ParsedArgument<?> value = fixed != null ? reader.parseFixed(argument, fixed) : reader.read(argument, specs);

            if (first.optional()) {
                optional.put(argument.getName(), value);
            } else {
                required.put(argument.getName(), value);
            }
        }

        for (int index = 0; index < path.size(); index++) {
            AbstractArgument<?, ?, ?, ?> argument = path.get(index);

            if (adapter.literal(argument) || !argument.isListed()) {
                continue;
            }

            String fixed = forced.get(argument);

            if (fixed != null) {
                required.put(argument.getName(), reader.parseFixed(argument, fixed));

                continue;
            }

            List<NodeSpec> specs = builder.treeSpecs(level, path.subList(0, index + 1));

            required.put(argument.getName(), reader.read(argument, specs));
        }

        return new CommandArguments<>(level, required, optional, fullInput, parent);
    }

    public static @NotNull String raw(@NotNull CommandContext<?> context, @NotNull String nodeName) {
        for (ParsedCommandNode<?> node : context.getNodes()) {
            if (node.getNode().getName().equals(nodeName)) {
                return node.getRange().get(context.getInput());
            }
        }

        return "";
    }

    public static @NotNull String raw(@NotNull CommandContext<?> context, @NotNull List<NodeSpec> specs) {
        StringBuilder joined = new StringBuilder();

        for (NodeSpec spec : specs) {
            if (joined.length() > 0) {
                joined.append(' ');
            }

            joined.append(raw(context, spec.name()));
        }

        return joined.toString();
    }

}
