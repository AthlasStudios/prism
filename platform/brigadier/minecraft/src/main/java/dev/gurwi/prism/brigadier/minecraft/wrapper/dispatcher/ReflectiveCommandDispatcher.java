package dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.BrigadierCompatibilityChecker;
import dev.gurwi.prism.brigadier.minecraft.bukkit.BrigadierBukkitCommand;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.bukkit.wrapper.WrappedCommandMap;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class ReflectiveCommandDispatcher<SourceStack> extends MinecraftCommandDispatcher<SourceStack> {

    private static final String[] NODE_MAPS = { "children", "literals", "arguments" };
    private static final String RELOAD_EVENT = "io.papermc.paper.event.server.ServerResourcesReloadedEvent";

    private final List<Entry> entries = new ArrayList<>();
    private final WrappedCommandMap commandMap;
    private boolean queued;

    protected ReflectiveCommandDispatcher(@NotNull Plugin plugin) throws NoSuchFieldException, IllegalAccessException {
        super(plugin);
        this.commandMap = new WrappedCommandMap(plugin.getServer());

        listen(ServerLoadEvent.class);

        Bukkit.getPluginManager().registerEvents(new JoinListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new CommandInterceptor(), plugin);

        if (BrigadierCompatibilityChecker.has(RELOAD_EVENT)) {
            try {
                listen(Class.forName(RELOAD_EVENT).asSubclass(Event.class));
            } catch (ClassNotFoundException ignored) {
            }
        }
    }

    public abstract CommandDispatcher<SourceStack> getVanillaDispatcher();

    public Command createBukkitCommand(MinecraftCommandDispatcher<SourceStack> own, LiteralCommandNode<SourceStack> mirrored, String description, List<String> aliases) {
        return new BrigadierBukkitCommand<>(own, mirrored, description, aliases);
    }

    public WrappedCommandMap getCommandMap() {
        return commandMap;
    }

    @Override
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
            report("register " + node.getName(), throwable);
        }
    }

    @Override
    public void unregister(@NotNull LiteralCommandNode<MinecraftCommandSource> node) {
        try {
            Entry target = null;

            for (Entry entry : entries) {
                if (entry.root() == node || entry.root().getName().equals(node.getName())) {
                    target = entry;
                    break;
                }
            }

            if (target == null) {
                removeChild(getRoot(), node.getName());
                return;
            }

            entries.remove(target);

            Map<String, Command> known = getCommandMap().getKnownCommands();
            CommandNode<SourceStack> vanillaRoot = getVanillaDispatcher().getRoot();

            for (String label : target.labels()) {
                release(known, vanillaRoot, label, target);
                release(known, vanillaRoot, target.prefix() + ":" + label, target);
            }

            removeChild(getRoot(), target.root().getName());
        } catch (Throwable throwable) {
            report("unregister " + node.getName(), throwable);
        }

        refreshPlayers();
    }

    private void release(Map<String, Command> known, CommandNode<SourceStack> vanillaRoot, String name, Entry entry) {
        try {
            if (known.get(name) != entry.command()) return;

            known.remove(name);
            removeChild(vanillaRoot, name);
        } catch (Throwable throwable) {
            report("release " + name, throwable);
        }
    }

    private void removeChild(CommandNode<?> parent, String name) throws ReflectiveOperationException {
        for (String field : NODE_MAPS) {
            Map<String, ?> map = ReflectionUtil.accessFieldFromType(CommandNode.class, parent, field);
            map.remove(name);
        }
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
                    claim(known, vanillaRoot, label, entry);
                    claim(known, vanillaRoot, entry.prefix() + ":" + label, entry);
                }
            }
        } catch (Throwable throwable) {
            report("reapply", throwable);
        }

        refreshPlayers();
    }

    private void claim(Map<String, Command> known, CommandNode<SourceStack> vanillaRoot, String name, Entry entry) {
        try {
            if (replace(vanillaRoot, name, entry.root())) {
                known.put(name, entry.command());
            }
        } catch (Throwable throwable) {
            report("claim " + name, throwable);
        }
    }

    private boolean replace(CommandNode<SourceStack> vanillaRoot, String name, LiteralCommandNode<MinecraftCommandSource> root) {
        LiteralCommandNode<SourceStack> mirrored;

        try {
            mirrored = mirror(root, name);
        } catch (Throwable throwable) {
            report("mirror " + name, throwable);
            return false;
        }

        List<Map<String, ?>> maps = new ArrayList<>();

        try {
            for (String field : NODE_MAPS) {
                Map<String, ?> map = ReflectionUtil.accessFieldFromType(CommandNode.class, vanillaRoot, field);
                maps.add(map);
            }
        } catch (ReflectiveOperationException exception) {
            report("reflect " + name, exception);
            return false;
        }

        CommandNode<SourceStack> previous = vanillaRoot.getChild(name);

        maps.forEach(map -> map.remove(name));

        try {
            vanillaRoot.addChild(mirrored);
            return true;
        } catch (Throwable throwable) {
            report("add " + name, throwable);

            if (previous != null) {
                vanillaRoot.addChild(previous);
            }

            return false;
        }
    }

    private void refreshPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                player.updateCommands();
            } catch (Throwable throwable) {
                report("updateCommands " + player.getName(), throwable);
            }
        }
    }

    private final class JoinListener implements Listener {

        @EventHandler
        public void onJoin(@NotNull PlayerJoinEvent event) {
            Player player = event.getPlayer();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    player.updateCommands();
                } catch (Throwable throwable) {
                    report("updateCommands " + player.getName(), throwable);
                }
            }, 1L);
        }

    }

    private final class CommandInterceptor implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {
            String message = event.getMessage();
            if (message.isEmpty() || message.charAt(0) != '/') return;

            String[] parts = message.substring(1).split(" ", -1);
            String label = parts[0].toLowerCase(Locale.ROOT);

            for (Entry entry : entries) {
                for (String own : entry.labels()) {
                    if (label.equals(own) || label.equals(entry.prefix() + ":" + own)) {
                        event.setCancelled(true);

                        String[] args = java.util.Arrays.copyOfRange(parts, 1, parts.length);
                        entry.command().execute(event.getPlayer(), label, args);
                        return;
                    }
                }
            }
        }

    }

    private record Entry(LiteralCommandNode<MinecraftCommandSource> root, Command command, String prefix, List<String> labels) {
    }

}