package dev.gurwi.prism.brigadier.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractBrigadierProvider<S> implements MinecraftCommandDispatcherProvider {

    private static final String[] NODE_MAPS = { "children", "literals", "arguments" };
    private static final String RELOAD_EVENT = "io.papermc.paper.event.server.ServerResourcesReloadedEvent";

    protected final Plugin plugin;
    private final CommandDispatcher<MinecraftBrigadierSource> dispatcher = new CommandDispatcher<>();
    private final NodeMirror<S> mirror;
    private final List<Entry> entries = new ArrayList<>();
    private boolean queued;

    private record Entry(LiteralCommandNode<MinecraftBrigadierSource> root, Command command, String prefix, List<String> labels) {
    }

    protected AbstractBrigadierProvider(Plugin plugin, Function<S, MinecraftBrigadierSource> wrapper) {
        this.plugin = plugin;
        this.mirror = new NodeMirror<>(dispatcher, wrapper);

        listen(ServerLoadEvent.class);

        if (BrigadierCompatibilityChecker.has(RELOAD_EVENT)) {
            try {
                listen(Class.forName(RELOAD_EVENT).asSubclass(Event.class));
            } catch (ClassNotFoundException ignored) {
            }
        }
    }

    protected abstract CommandDispatcher<S> vanillaDispatcher();

    protected abstract CommandMap commandMap();

    protected abstract Command createCommand(CommandDispatcher<MinecraftBrigadierSource> own, LiteralCommandNode<S> mirrored, String description, List<String> aliases);

    @Override
    public CommandDispatcher<MinecraftBrigadierSource> getDispatcher() {
        return dispatcher;
    }

    @Override
    public void register(LiteralCommandNode<MinecraftBrigadierSource> node, String description, List<String> aliases) {
        try {
            dispatcher.getRoot().addChild(node);

            LiteralCommandNode<MinecraftBrigadierSource> root = (LiteralCommandNode<MinecraftBrigadierSource>) dispatcher.getRoot().getChild(node.getName());
            Command command = createCommand(dispatcher, mirror.mirror(root, root.getName()), description, aliases);
            String prefix = plugin.getName().toLowerCase(Locale.ROOT);

            List<String> labels = new ArrayList<>();
            labels.add(root.getName().toLowerCase(Locale.ROOT));

            for (String alias : aliases) {
                labels.add(alias.toLowerCase(Locale.ROOT));
            }

            commandMap().register(prefix, command);

            entries.removeIf(entry -> entry.root() == root);
            entries.add(new Entry(root, command, prefix, labels));

            queue();
        } catch (Throwable throwable) {
            plugin.getLogger().warning("Brigadier provider: register failed for " + node.getName());
            throwable.printStackTrace();
        }
    }

    private void listen(Class<? extends Event> type) {
        Bukkit.getPluginManager().registerEvent(type, new Listener() {
        }, EventPriority.NORMAL, (listener, event) -> queue(), plugin);
    }

    private void queue() {
        if (queued) {
            return;
        }

        queued = true;

        Bukkit.getScheduler().runTask(plugin, () -> {
            queued = false;
            reapply();
        });
    }

    private void reapply() {
        try {
            Map<String, Command> known = commandMap().getKnownCommands();
            CommandNode<S> vanillaRoot = vanillaDispatcher().getRoot();

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

    private void replace(CommandNode<S> vanillaRoot, String name, LiteralCommandNode<MinecraftBrigadierSource> root) {
        for (String field : NODE_MAPS) {
            try {
                Map<String, ?> map = ReflectionUtil.accessFieldFromType(CommandNode.class, vanillaRoot, field);
                map.remove(name);
            } catch (ReflectiveOperationException exception) {
                plugin.getLogger().warning("Brigadier provider: failed to clear vanilla node " + name + ": " + exception);
            }
        }

        vanillaRoot.addChild(mirror.mirror(root, name));
    }

}