package dev.gurwi.prism.brigadier.minecraft;

import dev.gurwi.prism.brigadier.minecraft.adapter.CommandDefinitionAdapter;
import dev.gurwi.prism.brigadier.minecraft.wrapper.dispatcher.MinecraftCommandDispatcher;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public class BrigadierCommandRegistrar implements BukkitCommandRegistrar {

    private final CommandDefinitionAdapter adapter = new CommandDefinitionAdapter();
    private final MinecraftCommandDispatcher<?> dispatcher;

    public BrigadierCommandRegistrar(MinecraftCommandDispatcher<?> dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        for (String label : adapter.getBuilder().labels(definition)) {
            dispatcher.register(adapter.getBuilder().build(definition, label), definition.getDescription(), definition.getAliases());
        }
    }

    @Override
    public void unregister(BukkitCommandDefinition definition) {

    }

    @Override
    public @Unmodifiable Set<BukkitCommandDefinition> getRegisteredCommands() {
        return Set.of();
    }

    @Override
    public Type getType() {
        return Type.BRIGADIER;
    }

}
