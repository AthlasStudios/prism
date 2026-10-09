package dev.gurwi.prism.brigadier.minecraft.wrapper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.BrigadierCompatibilityChecker;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class MinecraftCommandDispatcher<SourceStack> extends CommandDispatcher<MinecraftCommandSource> {

    private static final String[] NODE_MAPS = { "children", "literals", "arguments" };
    private static final String RELOAD_EVENT = "io.papermc.paper.event.server.ServerResourcesReloadedEvent";

    private final List<Entry> entries = new ArrayList<>();
    private boolean queued;

    private final Plugin plugin;
    private final WrappedCommandMap commandMap;

    protected MinecraftCommandDispatcher(@NotNull Plugin plugin) throws NoSuchFieldException, IllegalAccessException {
        this.plugin = plugin;
        this.commandMap = new WrappedCommandMap(plugin.getServer()); // TODO -> REMOVE DUPLICATE CODE, LEGACYCOMMANDREGISTRAR REQUIRES THIS TOO

        listen(ServerLoadEvent.class);

        if (BrigadierCompatibilityChecker.has(RELOAD_EVENT)) {
            try {
                listen(Class.forName(RELOAD_EVENT).asSubclass(Event.class));
            } catch (ClassNotFoundException ignored) {
            }
        }
    }

    public abstract MinecraftCommandSource toMinecraftSource(SourceStack source);

    public abstract CommandDispatcher<SourceStack> getVanillaDispatcher();

    public WrappedCommandMap getCommandMap() {
        return commandMap;
    }

    public abstract Command createBukkitCommand(MinecraftCommandDispatcher<SourceStack> own, LiteralCommandNode<SourceStack> mirrored, String description, List<String> aliases);

    public void register(@NotNull LiteralCommandNode<MinecraftCommandSource> node, String description, @NotNull List<String> aliases) {
        try {
            getRoot().addChild(node);

            LiteralCommandNode<MinecraftCommandSource> root = (LiteralCommandNode<MinecraftCommandSource>) getRoot().getChild(node.getName());
            Command command = createBukkitCommand(this, mirror(root, root.getName()), description, aliases);
            String prefix = plugin.getName().toLowerCase(Locale.ROOT);

            List<String> labels = new ArrayList<>();
            labels.add(root.getName().toLowerCase(Locale.ROOT));

            for (String alias : aliases) {
                labels.add(alias.toLowerCase(Locale.ROOT));
            }

            getCommandMap().register(prefix, command);

            entries.removeIf(entry -> entry.root() == root);
            entries.add(new Entry(root, command, prefix, labels));

            queue();
        } catch (Throwable throwable) {
            plugin.getLogger().warning("Brigadier provider: register failed for " + node.getName());
            throwable.printStackTrace();
        }
    }

    public @NotNull LiteralCommandNode<SourceStack> mirror(@NotNull LiteralCommandNode<MinecraftCommandSource> node, @NotNull String label) {
        return (LiteralCommandNode<SourceStack>) mapNode(node, label);
    }

    private CommandNode<SourceStack> mapNode(@NotNull CommandNode<MinecraftCommandSource> node, @Nullable String label) {
        if (node.getRedirect() != null) {
            throw new UnsupportedOperationException("Redirects are not supported by the Minecraft mirror: " + node.getName());
        }

        ArgumentBuilder<SourceStack, ?> builder;

        if (node instanceof LiteralCommandNode<MinecraftCommandSource> literalNode) {
            builder = LiteralArgumentBuilder.literal(label == null ? literalNode.getLiteral() : label);
        } else if (node instanceof ArgumentCommandNode<MinecraftCommandSource, ?> argumentNode) {
            builder = mapArgument(argumentNode);
        } else {
            throw new IllegalArgumentException("Unsupported node type: " + node.getClass());
        }

        builder.requires(stack -> node.getRequirement().test(toMinecraftSource(stack)));

        if (node.getCommand() != null) {
            builder.executes(context -> node.getCommand().run(mapContext(context)));
        }

        for (CommandNode<MinecraftCommandSource> child : node.getChildren()) {
            builder.then(mapNode(child, null));
        }

        return builder.build();
    }

    private <T> @NotNull RequiredArgumentBuilder<SourceStack, T> mapArgument(@NotNull ArgumentCommandNode<MinecraftCommandSource, T> node) {
        RequiredArgumentBuilder<SourceStack, T> builder = RequiredArgumentBuilder.argument(node.getName(), node.getType());

        if (node.getCustomSuggestions() != null) {
            builder.suggests((context, suggestions) ->
                    node.getCustomSuggestions().getSuggestions(mapContext(context), suggestions)
            );
        }

        return builder;
    }

    private @NotNull CommandContext<MinecraftCommandSource> mapContext(@NotNull CommandContext<SourceStack> context) {
        Map<String, ParsedArgument<MinecraftCommandSource, ?>> arguments = new HashMap<>();

        for (ParsedCommandNode<SourceStack> parsed : context.getNodes()) {
            if (parsed.getNode() instanceof ArgumentCommandNode<SourceStack, ?> argumentNode) {
                String name = argumentNode.getName();

                arguments.put(name, new ParsedArgument<>(
                        parsed.getRange().getStart(),
                        parsed.getRange().getEnd(),
                        context.getArgument(name, Object.class)
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
                child == null ? null : mapContext(child),
                null,
                context.isForked()
        );
    }

    private void listen(Class<? extends Event> type) {
        Bukkit.getPluginManager().registerEvent(type, new Listener() { }, EventPriority.NORMAL, (listener, event) -> queue(), plugin);
    }

    private void queue() {
        if (queued) return;
        queued = true;

        Bukkit.getScheduler().runTask(plugin, () -> {
            queued = false;
            reapply();
        });
    }

    private void reapply() {
        try {
            Map<String, Command> known = getCommandMap().getKnownCommands();
            CommandNode<SourceStack> vanillaRoot = getVanillaDispatcher().getRoot();

            for (Entry entry : entries) {
                for (String label : entry.labels()) {
                    String prefixed = entry.prefix() + ":" + label;

                    known.put(label, entry.command());
                    known.put(prefixed, entry.command());

                    replace(vanillaRoot, label, entry.root());
                    replace(vanillaRoot, prefixed, entry.root());
                }
            }

            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        } catch (Throwable throwable) {
            plugin.getLogger().warning("Brigadier provider: reapply failed");
            throwable.printStackTrace();
        }
    }

    private void replace(CommandNode<SourceStack> vanillaRoot, String name, LiteralCommandNode<MinecraftCommandSource> root) {
        for (String field : NODE_MAPS) {
            try {
                Map<String, ?> map = ReflectionUtil.accessFieldFromType(CommandNode.class, vanillaRoot, field);
                map.remove(name);
            } catch (ReflectiveOperationException exception) {
                plugin.getLogger().warning("Brigadier provider: failed to clear vanilla node " + name + ": " + exception);
            }
        }

        vanillaRoot.addChild(mirror(root, name));
    }

    private record Entry(LiteralCommandNode<MinecraftCommandSource> root, Command command, String prefix, List<String> labels) {
    }

}