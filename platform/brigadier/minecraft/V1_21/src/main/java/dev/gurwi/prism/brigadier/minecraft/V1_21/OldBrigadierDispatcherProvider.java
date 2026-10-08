package dev.gurwi.prism.brigadier.minecraft.V1_21;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.BrigadierCompatibilityChecker;
import dev.gurwi.prism.brigadier.minecraft.MinecraftBrigadierSource;
import dev.gurwi.prism.brigadier.minecraft.NodeMirror;
import dev.gurwi.prism.reflection.ReflectionUtil;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OldBrigadierDispatcherProvider implements dev.gurwi.prism.brigadier.minecraft.MinecraftCommandDispatcherProvider {

    private static final String[] NODE_MAPS = {"children", "literals", "arguments"};

    private final Plugin plugin;
    private final CommandDispatcher<MinecraftBrigadierSource> dispatcher = new CommandDispatcher<>();
    private final NodeMirror<CommandSourceStack> mirror = new NodeMirror<>(
            dispatcher,
            stack -> MinecraftBrigadierSource.delegate(stack::getBukkitSender, stack::getExecutor)
    );
    private final List<Entry> entries = new ArrayList<>();
    private boolean queued;

    private record Entry(LiteralCommandNode<MinecraftBrigadierSource> root, VanillaCommand command, String prefix, List<String> labels) {
    }

    public OldBrigadierDispatcherProvider(Plugin plugin) {
        this.plugin = plugin;

        try {
            Bukkit.getPluginManager().registerEvents(new LoadListener(), plugin);

            if (BrigadierCompatibilityChecker.has("io.papermc.paper.event.server.ServerResourcesReloadedEvent")) {
                Bukkit.getPluginManager().registerEvents(new ResourcesListener(), plugin);
            }
        } catch (Throwable throwable) {
            plugin.getLogger().warning("Brigadier provider: listener registration failed");
            throwable.printStackTrace();
        }
    }

    @Override
    public CommandDispatcher<MinecraftBrigadierSource> getDispatcher() {
        return dispatcher;
    }

    @Override
    public void register(LiteralCommandNode<MinecraftBrigadierSource> node, String description, List<String> aliases) {
        plugin.getLogger().info("Brigadier provider: register " + node.getName());

        try {
            dispatcher.getRoot().addChild(node);

            LiteralCommandNode<MinecraftBrigadierSource> root = (LiteralCommandNode<MinecraftBrigadierSource>) dispatcher.getRoot().getChild(node.getName());
            VanillaCommand command = new VanillaCommand(dispatcher, mirror.mirror(root, root.getName()), description, aliases);
            String prefix = plugin.getName().toLowerCase(Locale.ROOT);

            List<String> labels = new ArrayList<>();
            labels.add(root.getName().toLowerCase(Locale.ROOT));

            for (String alias : aliases) {
                labels.add(alias.toLowerCase(Locale.ROOT));
            }

            boolean registered = ((CraftServer) Bukkit.getServer()).getCommandMap().register(prefix, command);

            plugin.getLogger().info("Brigadier provider: command map register " + node.getName() + " -> " + registered);

            entries.removeIf(entry -> entry.root() == root);
            entries.add(new Entry(root, command, prefix, labels));

            queue();
        } catch (Throwable throwable) {
            plugin.getLogger().warning("Brigadier provider: register failed for " + node.getName());
            throwable.printStackTrace();
        }
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
            Map<String, Command> known = ((CraftServer) Bukkit.getServer()).getCommandMap().getKnownCommands();
            CommandNode<CommandSourceStack> vanillaRoot = MinecraftServer.getServer().getCommands().getDispatcher().getRoot();

            for (Entry entry : entries) {
                for (String label : entry.labels()) {
                    String prefixed = entry.prefix() + ":" + label;

                    known.put(label, entry.command());
                    known.put(prefixed, entry.command());

                    replace(vanillaRoot, label, entry.root());
                    replace(vanillaRoot, prefixed, entry.root());

                    plugin.getLogger().info("Brigadier provider: " + label + " map=" + known.get(label).getClass().getSimpleName() + " vanilla=" + vanillaRoot.getChild(label));
                }
            }

            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        } catch (Throwable throwable) {
            plugin.getLogger().warning("Brigadier provider: reapply failed");
            throwable.printStackTrace();
        }
    }

    private void replace(CommandNode<CommandSourceStack> vanillaRoot, String name, LiteralCommandNode<MinecraftBrigadierSource> root) {
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

    private final class LoadListener implements Listener {

        @EventHandler
        public void onLoad(ServerLoadEvent event) {
            queue();
        }

    }

    private final class ResourcesListener implements Listener {

        @EventHandler
        public void onReload(ServerResourcesReloadedEvent event) {
            queue();
        }

    }

}