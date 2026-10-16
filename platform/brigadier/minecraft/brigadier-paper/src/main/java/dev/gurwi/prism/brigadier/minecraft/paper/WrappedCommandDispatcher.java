package dev.gurwi.prism.brigadier.minecraft.paper;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.reflection.ReflectionUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class WrappedCommandDispatcher extends MinecraftCommandDispatcher<CommandSourceStack> {

    private static final String[] NODE_MAPS = { "children", "literals", "arguments" };

    private final List<Entry> entries = new CopyOnWriteArrayList<>();
    private volatile boolean lifecycleFired;

    protected WrappedCommandDispatcher(@NotNull Plugin plugin) {
        super(plugin);

        plugin.getLogger().info("[prism] Using paper Lifecycle Manager.");

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();

            for (Entry entry : entries) {
                String name = entry.root().getName();

                try {
                    registrar.register(mirror(entry.root(), name), entry.description(), entry.aliases());
                } catch (Throwable throwable) {
                    report("paper register " + name, throwable);
                }
            }

            lifecycleFired = true;
        });
    }

    @Override
    public MinecraftCommandSource toMinecraftSource(CommandSourceStack source) {
        return new WrappedSourceStack(source);
    }

    @Override
    public void register(@NotNull LiteralCommandNode<MinecraftCommandSource> node, String description, @NotNull List<String> aliases) {
        try {
            getRoot().addChild(node);

            LiteralCommandNode<MinecraftCommandSource> root = (LiteralCommandNode<MinecraftCommandSource>) getRoot().getChild(node.getName());
            Entry entry = new Entry(root, description, List.copyOf(aliases));

            entries.removeIf(existing -> existing.root() == root);
            entries.add(entry);

            if (lifecycleFired) {
                registerLive(entry);
            }
        } catch (Throwable throwable) {
            report("register " + node.getName(), throwable);
        }
    }

    @Override
    public void unregister(@NotNull LiteralCommandNode<MinecraftCommandSource> node) {
        try {
            String name = node.getName();
            Entry target = null;

            for (Entry entry : entries) {
                if (entry.root() == node || entry.root().getName().equals(name)) {
                    target = entry;
                    break;
                }
            }

            if (target != null) {
                entries.remove(target);
            }

            removeChild(getRoot(), name);

            if (target != null) {
                CommandNode<?> vanillaRoot = vanillaRoot();

                for (String label : labels(target)) {
                    removeChild(vanillaRoot, label);
                    removeChild(vanillaRoot, prefix() + ":" + label);
                }
            }

            refreshPlayers();
        } catch (Throwable throwable) {
            report("unregister " + node.getName(), throwable);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerLive(@NotNull Entry entry) throws ReflectiveOperationException {
        CommandNode vanillaRoot = vanillaRoot();

        for (String label : labels(entry)) {
            for (String key : List.of(label, prefix() + ":" + label)) {
                removeChild(vanillaRoot, key);
                vanillaRoot.addChild(mirror(entry.root(), key));
            }
        }

        refreshPlayers();
    }

    private CommandNode<?> vanillaRoot() {
        return MinecraftServer.getServer().getCommands().getDispatcher().getRoot();
    }

    private String prefix() {
        return plugin.getName().toLowerCase(Locale.ROOT);
    }

    private @NotNull List<String> labels(@NotNull Entry entry) {
        List<String> labels = new ArrayList<>();
        labels.add(entry.root().getName());
        labels.addAll(entry.aliases());
        return labels;
    }

    private void refreshPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.updateCommands();
        }
    }

    private static void removeChild(CommandNode<?> parent, String name) throws ReflectiveOperationException {
        for (String field : NODE_MAPS) {
            Map<String, ?> map = ReflectionUtil.accessFieldFromType(CommandNode.class, parent, field);
            map.remove(name);
        }
    }

    private record Entry(LiteralCommandNode<MinecraftCommandSource> root, String description, List<String> aliases) {
    }

}