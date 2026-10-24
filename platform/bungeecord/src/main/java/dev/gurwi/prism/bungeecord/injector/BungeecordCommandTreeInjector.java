package dev.gurwi.prism.bungeecord.injector;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;

public final class BungeecordCommandTreeInjector implements Listener {

    private static final String HANDLER_NAME = "prism-commands";
    private static final String DEFAULT_ENCODER = "packet-encoder";

    private final Plugin plugin;
    private final Supplier<Map<String, LiteralCommandNode<CommandSender>>> nodes;
    private final Class<?> commandsType;
    private final Field rootField;
    private final Method channelAccessor;
    private final Method handleAccessor;
    private final String encoderName;

    private BungeecordCommandTreeInjector(@NotNull Plugin plugin, @NotNull Supplier<Map<String, LiteralCommandNode<CommandSender>>> nodes, @NotNull Class<?> commandsType, @NotNull Field rootField, @NotNull Method channelAccessor, @NotNull Method handleAccessor, @NotNull String encoderName) {
        this.plugin = plugin;
        this.nodes = nodes;
        this.commandsType = commandsType;
        this.rootField = rootField;
        this.channelAccessor = channelAccessor;
        this.handleAccessor = handleAccessor;
        this.encoderName = encoderName;
    }

    public static @Nullable BungeecordCommandTreeInjector create(@NotNull Plugin plugin, @NotNull Supplier<Map<String, LiteralCommandNode<CommandSender>>> nodes) {
        try {
            Class.forName("io.netty.channel.ChannelOutboundHandlerAdapter");

            Class<?> commandsType = Class.forName("net.md_5.bungee.protocol.packet.Commands");
            Field rootField = commandsType.getDeclaredField("root");

            if (rootField.getType() != RootCommandNode.class) {
                plugin.getLogger().info("Prism command tree injection disabled: unsupported Brigadier layout");
                return null;
            }

            rootField.setAccessible(true);

            Class<?> connectionType = Class.forName("net.md_5.bungee.UserConnection");
            Method channelAccessor = connectionType.getMethod("getCh");
            Method handleAccessor = channelAccessor.getReturnType().getMethod("getHandle");

            BungeecordCommandTreeInjector injector = new BungeecordCommandTreeInjector(plugin, nodes, commandsType, rootField, channelAccessor, handleAccessor, resolveEncoderName());

            plugin.getProxy().getPluginManager().registerListener(plugin, injector);

            return injector;
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.INFO, "Prism command tree injection disabled: " + throwable);
            return null;
        }
    }

    @EventHandler
    public void onPostLogin(@NotNull PostLoginEvent event) {
        try {
            Object channel = handleAccessor.invoke(channelAccessor.invoke(event.getPlayer()));
            NettyInstaller.install(channel, encoderName, HANDLER_NAME, new NettyInstaller.Rewriter(this, event.getPlayer()));
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, "Prism failed to hook " + event.getPlayer().getName(), throwable);
        }
    }

    boolean isCommands(@Nullable Object message) {
        return commandsType.isInstance(message);
    }

    void rewrite(@NotNull ProxiedPlayer player, @NotNull Object packet) {
        try {
            Map<String, LiteralCommandNode<CommandSender>> owned = nodes.get();

            if (owned.isEmpty()) return;

            rootField.set(packet, merge(player, (RootCommandNode<?>) rootField.get(packet), owned));
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, "Prism failed to inject the command tree for " + player.getName(), throwable);
        }
    }

    @SuppressWarnings("unchecked")
    private @NotNull RootCommandNode<Object> merge(@NotNull ProxiedPlayer player, @NotNull RootCommandNode<?> original, @NotNull Map<String, LiteralCommandNode<CommandSender>> owned) {
        RootCommandNode<Object> root = new RootCommandNode<>();
        SuggestionProvider<Object> askServer = null;
        Set<String> names = new HashSet<>(owned.keySet());

        for (CommandNode<?> child : original.getChildren()) {
            if (!names.contains(child.getName().toLowerCase(Locale.ROOT))) {
                root.addChild((CommandNode<Object>) child);
                continue;
            }

            CommandNode<?> args = child.getChild("args");

            if (askServer == null && args instanceof ArgumentCommandNode<?, ?>) {
                askServer = (SuggestionProvider<Object>) ((ArgumentCommandNode<?, ?>) args).getCustomSuggestions();
            }
        }

        for (LiteralCommandNode<CommandSender> node : owned.values()) {
            if (node.canUse(player)) {
                root.addChild(copy(node, player, askServer));
            }
        }

        return root;
    }

    private @NotNull CommandNode<Object> copy(@NotNull CommandNode<CommandSender> node, @NotNull CommandSender source, @Nullable SuggestionProvider<Object> askServer) {
        ArgumentBuilder<Object, ?> builder;

        if (node instanceof ArgumentCommandNode<?, ?>) {
            ArgumentCommandNode<CommandSender, ?> argument = (ArgumentCommandNode<CommandSender, ?>) node;
            RequiredArgumentBuilder<Object, ?> required = required(argument.getName(), argument.getType());

            if (argument.getCustomSuggestions() != null && askServer != null) {
                required.suggests(askServer);
            }

            builder = required;
        } else {
            builder = LiteralArgumentBuilder.literal(node.getName());
        }

        if (node.getCommand() != null) {
            builder.executes(context -> 0);
        }

        for (CommandNode<CommandSender> child : node.getChildren()) {
            if (child.canUse(source)) {
                builder.then(copy(child, source, askServer));
            }
        }

        return builder.build();
    }

    private static @NotNull String resolveEncoderName() {
        try {
            Object value = Class.forName("net.md_5.bungee.netty.PipelineUtils").getField("MINECRAFT_ENCODER").get(null);

            return value instanceof String ? (String) value : DEFAULT_ENCODER;
        } catch (Throwable throwable) {
            return DEFAULT_ENCODER;
        }
    }

    private static <T> @NotNull RequiredArgumentBuilder<Object, T> required(@NotNull String name, @NotNull ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

}