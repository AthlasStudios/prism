package dev.gurwi.prism.velocity;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.platform.CommandsRegistry;
import dev.gurwi.prism.velocity.adapter.VelocityCommandDefinitionAdapter;
import dev.gurwi.prism.velocity.command.VelocityCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class VelocityCommandsRegistry implements CommandsRegistry<VelocityPlatform, VelocityCommandDefinition> {

    private final VelocityCommandDefinitionAdapter adapter = new VelocityCommandDefinitionAdapter();
    private final Map<VelocityCommandDefinition, Set<String>> registeredCommands = new LinkedHashMap<>();

    private final VelocityPlatform platform;

    public VelocityCommandsRegistry(@NotNull VelocityPlatform platform) {
        this.platform = platform;
    }

    @Override
    public synchronized void register(VelocityCommandDefinition definition) {
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
    public synchronized void unregister(VelocityCommandDefinition definition) {
        Set<String> labels = registeredCommands.remove(definition);
        if (labels == null) return;

        CommandManager manager = platform.getServer().getCommandManager();
        labels.forEach(manager::unregister);
    }

    @Override
    public synchronized @Unmodifiable Set<VelocityCommandDefinition> getRegisteredCommands() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(registeredCommands.keySet()));
    }

    @Override
    public VelocityPlatform getPlatform() {
        return platform;
    }

}