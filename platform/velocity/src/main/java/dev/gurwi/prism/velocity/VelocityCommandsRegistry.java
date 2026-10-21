package dev.gurwi.prism.velocity;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import dev.gurwi.prism.platform.CommandsRegistry;
import dev.gurwi.prism.velocity.adapter.VelocityCommandDefinitionAdapter;
import dev.gurwi.prism.velocity.command.VelocityCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class VelocityCommandsRegistry implements CommandsRegistry<VelocityPlatform, VelocityCommandDefinition> {

    private final VelocityCommandDefinitionAdapter adapter = new VelocityCommandDefinitionAdapter();
    private final Map<VelocityCommandDefinition, Set<String>> registeredCommands = new LinkedHashMap<>();
    private final Set<VelocityCommandDefinition> pending = new LinkedHashSet<>();

    private final VelocityPlatform platform;
    private boolean initialized;

    public VelocityCommandsRegistry(@NotNull VelocityPlatform platform) {
        this.platform = platform;
        platform.getServer().getEventManager().register(platform.getPlugin(), this);
    }

    @Override
    public synchronized void register(VelocityCommandDefinition definition) {
        if (!initialized) {
            pending.add(definition);
            return;
        }

        doRegister(definition);
    }

    @Override
    public synchronized void unregister(VelocityCommandDefinition definition) {
        if (pending.remove(definition)) return;

        Set<String> labels = registeredCommands.remove(definition);
        if (labels == null) return;

        CommandManager manager = platform.getServer().getCommandManager();
        labels.forEach(manager::unregister);
    }

    @Subscribe
    public synchronized void onProxyInit(ProxyInitializeEvent event) {
        if (initialized) return;
        initialized = true;

        List<VelocityCommandDefinition> queued = new ArrayList<>(pending);
        pending.clear();

        queued.forEach(this::doRegister);
    }

    private void doRegister(VelocityCommandDefinition definition) {
        CommandManager manager = platform.getServer().getCommandManager();
        Set<String> labels = registeredCommands.computeIfAbsent(definition, _ -> new HashSet<>());

        for (String label : adapter.getBuilder().labels(definition)) {
            LiteralCommandNode<CommandSource> node = adapter.getBuilder().build(definition, label);
            BrigadierCommand command = new BrigadierCommand(node);

            CommandMeta meta = manager.metaBuilder(command)
                    .plugin(platform.getPlugin())
                    .build();

            manager.register(meta, command);
            labels.add(node.getName());
        }
    }

    @Override
    public synchronized @Unmodifiable Set<VelocityCommandDefinition> getRegisteredCommands() {
        Set<VelocityCommandDefinition> all = new LinkedHashSet<>(registeredCommands.keySet());
        all.addAll(pending);
        return Collections.unmodifiableSet(all);
    }

    @Override
    public VelocityPlatform getPlatform() {
        return platform;
    }

}