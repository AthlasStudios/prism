package dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.argument.NativeBackedArgumentType;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MinecraftCommandDispatcher<SourceStack> extends CommandDispatcher<MinecraftCommandSource> {

    private static final SimpleCommandExceptionType INTERNAL_ERROR = new SimpleCommandExceptionType(new LiteralMessage("An internal error occurred while running this command"));

    protected final Plugin plugin;
    private final Set<String> reported = ConcurrentHashMap.newKeySet();

    protected MinecraftCommandDispatcher(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract MinecraftCommandSource toMinecraftSource(SourceStack source);

    public abstract void register(@NotNull LiteralCommandNode<MinecraftCommandSource> node, String description, @NotNull List<String> aliases);

    public @NotNull LiteralCommandNode<SourceStack> mirror(@NotNull LiteralCommandNode<MinecraftCommandSource> node, @NotNull String label) {
        return (LiteralCommandNode<SourceStack>) mapNode(node, label, Map.of());
    }

    private CommandNode<SourceStack> mapNode(@NotNull CommandNode<MinecraftCommandSource> node, @Nullable String label, @NotNull Map<String, NativeBackedArgumentType<?, ?>> converters) {
        if (node.getRedirect() != null) {
            throw new UnsupportedOperationException("Redirects are not supported by the Minecraft mirror: " + node.getName());
        }

        Map<String, NativeBackedArgumentType<?, ?>> scope = scopeFor(node, converters);
        ArgumentBuilder<SourceStack, ?> builder;

        if (node instanceof LiteralCommandNode<MinecraftCommandSource> literalNode) {
            builder = LiteralArgumentBuilder.literal(label == null ? literalNode.getLiteral() : label);
        } else if (node instanceof ArgumentCommandNode<MinecraftCommandSource, ?> argumentNode) {
            builder = mapArgument(argumentNode, scope);
        } else {
            throw new IllegalArgumentException("Unsupported node type: " + node.getClass());
        }

        builder.requires(stack -> {
            try {
                return node.getRequirement().test(toMinecraftSource(stack));
            } catch (Throwable throwable) {
                report("requirement " + node.getName(), throwable);
                return false;
            }
        });

        builder.requires(stack -> {
            try {
                return node.getRequirement().test(toMinecraftSource(stack));
            } catch (Throwable throwable) {
                report("requirement " + node.getName(), throwable);
                return false;
            }
        });

        if (node.getCommand() != null) {
            builder.executes(context -> {
                try {
                    return node.getCommand().run(mapContext(context, scope));
                } catch (CommandSyntaxException exception) {
                    throw exception;
                } catch (Throwable throwable) {
                    report("executor " + node.getName(), throwable);
                    throw INTERNAL_ERROR.create();
                }
            });
        }

        for (CommandNode<MinecraftCommandSource> child : node.getChildren()) {
            builder.then(mapNode(child, null, scope));
        }

        return builder.build();
    }

    private Map<String, NativeBackedArgumentType<?, ?>> scopeFor(@NotNull CommandNode<MinecraftCommandSource> node, @NotNull Map<String, NativeBackedArgumentType<?, ?>> converters) {
        if (node instanceof ArgumentCommandNode<MinecraftCommandSource, ?> argumentNode && argumentNode.getType() instanceof NativeBackedArgumentType<?, ?> backed) {
            Map<String, NativeBackedArgumentType<?, ?>> scope = new HashMap<>(converters);
            scope.put(argumentNode.getName(), backed);

            return scope;
        }

        return converters;
    }

    private @NotNull RequiredArgumentBuilder<SourceStack, ?> mapArgument(@NotNull ArgumentCommandNode<MinecraftCommandSource, ?> node, @NotNull Map<String, NativeBackedArgumentType<?, ?>> scope) {
        ArgumentType<?> type = node.getType() instanceof NativeBackedArgumentType<?, ?> backed ? backed.nativeType() : node.getType();
        RequiredArgumentBuilder<SourceStack, ?> builder = argument(node.getName(), type);

        if (node.getCustomSuggestions() != null) {
            builder.suggests((context, suggestions) -> {
                try {
                    return node.getCustomSuggestions().getSuggestions(mapContext(context, scope), suggestions);
                } catch (Throwable throwable) {
                    report("suggestions " + node.getName(), throwable);
                    return suggestions.buildFuture();
                }
            });
        }

        return builder;
    }

    @Contract(value = "_, _ -> new", pure = true)
    private <T> @NotNull RequiredArgumentBuilder<SourceStack, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    private @NotNull CommandContext<MinecraftCommandSource> mapContext(@NotNull CommandContext<SourceStack> context, @NotNull Map<String, NativeBackedArgumentType<?, ?>> converters) throws CommandSyntaxException {
        Map<String, ParsedArgument<MinecraftCommandSource, ?>> arguments = new HashMap<>();

        for (ParsedCommandNode<SourceStack> parsed : context.getNodes()) {
            if (parsed.getNode() instanceof ArgumentCommandNode<SourceStack, ?> argumentNode) {
                String name = argumentNode.getName();
                Object value = context.getArgument(name, Object.class);
                NativeBackedArgumentType<?, ?> backed = converters.get(name);

                Object converted;

                try {
                    converted = backed == null ? value : backed.convertNative(value);
                } catch (CommandSyntaxException exception) {
                    throw withContext(exception, context.getInput(), parsed.getRange().getStart());
                }

                arguments.put(name, new ParsedArgument<>(
                        parsed.getRange().getStart(),
                        parsed.getRange().getEnd(),
                        converted
                ));
            }
        }

        CommandContext<SourceStack> child = context.getChild();

        return new CommandContext<>(
                toMinecraftSource(context.getSource()),
                context.getInput(),
                arguments,
                null,
                getRoot(),
                List.of(),
                context.getRange(),
                child == null ? null : mapContext(child, converters),
                null,
                context.isForked()
        );
    }

    private static @NotNull CommandSyntaxException withContext(@NotNull CommandSyntaxException exception, String input, int cursor) {
        if (exception.getInput() != null) {
            return exception;
        }

        return new CommandSyntaxException(exception.getType(), exception.getRawMessage(), input, cursor);
    }

    protected void report(String where, Throwable throwable) {
        if (reported.add(where)) {
            plugin.getLogger().warning("Brigadier provider: " + where + " failed, continuing without it: " + throwable);
            throwable.printStackTrace();
        }
    }

}