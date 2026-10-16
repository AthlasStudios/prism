package dev.gurwi.prism.brigadier.minecraft;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.gurwi.prism.brigadier.minecraft.adapter.BukkitCommandDefinitionAdapter;
import dev.gurwi.prism.brigadier.minecraft.wrapper.MinecraftCommandSource;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class BrigadierCommandRegistrar implements BukkitCommandRegistrar {

    private final BukkitCommandDefinitionAdapter adapter = new BukkitCommandDefinitionAdapter();
    private final Map<BukkitCommandDefinition, Set<LiteralCommandNode<MinecraftCommandSource>>> registeredCommands = new HashMap<>();

    private final MinecraftCommandDispatcher<?> dispatcher;

    public BrigadierCommandRegistrar(MinecraftCommandDispatcher<?> dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        for (String label : adapter.getBuilder().labels(definition)) {
            LiteralCommandNode<MinecraftCommandSource> brigadierCommand = adapter.getBuilder().build(definition, label);
            dispatcher.register(brigadierCommand, definition.getDescription(), definition.getAliases());

            registeredCommands.computeIfAbsent(definition, k -> new HashSet<>()).add(brigadierCommand);
        }
    }

    @Override
    public void unregister(BukkitCommandDefinition definition) {
        registeredCommands.remove(definition).forEach(dispatcher::unregister);
    }

    @Override
    public @Unmodifiable Set<BukkitCommandDefinition> getRegisteredCommands() {
        return Collections.unmodifiableSet(registeredCommands.keySet());
    }

    @Override
    public Type getType() {
        return Type.BRIGADIER;
    }

}
