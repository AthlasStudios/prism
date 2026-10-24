package dev.gurwi.prism.bungeecord;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.bungeecord.adapter.BungeecordCommandDefinitionAdapter;
import dev.gurwi.prism.bungeecord.command.BungeecordCommandDefinition;
import dev.gurwi.prism.bungeecord.command.BungeecordCommandShim;
import dev.gurwi.prism.bungeecord.injector.BungeecordCommandTreeInjector;
import dev.gurwi.prism.platform.CommandsRegistry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class BungeecordCommandsRegistry implements CommandsRegistry<BungeecordPlatform, BungeecordCommandDefinition> {

    private final BungeecordCommandDefinitionAdapter adapter = new BungeecordCommandDefinitionAdapter();
    private final Map<BungeecordCommandDefinition, Map<String, BungeecordCommandShim>> registeredCommands = new LinkedHashMap<>();
    private final Map<String, LiteralCommandNode<CommandSender>> nodes = new LinkedHashMap<>();

    private final BungeecordPlatform platform;
    private final BungeecordCommandTreeInjector injector;

    public BungeecordCommandsRegistry(@NotNull BungeecordPlatform platform) {
        this.platform = platform;
        this.injector = platform.getConfiguration().isCommandTreeInjectionEnabled()
                ? BungeecordCommandTreeInjector.create(platform.getPlugin(), this::snapshot)
                : null;
    }

    @Override
    public synchronized void register(BungeecordCommandDefinition definition) {
        if (registeredCommands.containsKey(definition)) {
            unregister(definition);
        }

        Map<String, LiteralCommandNode<CommandSender>> built = new LinkedHashMap<>();

        for (String label : adapter.getBuilder().labels(definition)) {
            built.put(label, adapter.getBuilder().build(definition, label));
        }

        PluginManager manager = platform.getServer().getPluginManager();
        Map<String, BungeecordCommandShim> shims = new LinkedHashMap<>();

        for (Map.Entry<String, LiteralCommandNode<CommandSender>> entry : built.entrySet()) {
            BungeecordCommandShim shim = new BungeecordCommandShim(entry.getValue(), platform.getPlugin().getLogger());

            manager.registerCommand(platform.getPlugin(), shim);

            shims.put(entry.getKey(), shim);
            nodes.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
        }

        registeredCommands.put(definition, shims);
    }

    @Override
    public synchronized void unregister(BungeecordCommandDefinition definition) {
        Map<String, BungeecordCommandShim> shims = registeredCommands.remove(definition);
        if (shims == null) return;

        PluginManager manager = platform.getServer().getPluginManager();

        for (Map.Entry<String, BungeecordCommandShim> entry : shims.entrySet()) {
            manager.unregisterCommand(entry.getValue());
            nodes.remove(entry.getKey().toLowerCase(Locale.ROOT));
        }
    }

    @Override
    public synchronized @Unmodifiable Set<BungeecordCommandDefinition> getRegisteredCommands() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(registeredCommands.keySet()));
    }

    @Override
    public BungeecordPlatform getPlatform() {
        return platform;
    }

    public BungeecordCommandTreeInjector getInjector() {
        return injector;
    }

    private synchronized @NotNull Map<String, LiteralCommandNode<CommandSender>> snapshot() {
        return new LinkedHashMap<>(nodes);
    }

}
