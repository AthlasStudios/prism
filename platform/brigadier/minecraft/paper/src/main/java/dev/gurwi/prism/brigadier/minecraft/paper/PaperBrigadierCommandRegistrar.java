package dev.gurwi.prism.brigadier.minecraft.paper;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public class PaperBrigadierCommandRegistrar implements BukkitCommandRegistrar {

    private final Set<BukkitCommandDefinition> registeredCommands = Set.of();

    public PaperBrigadierCommandRegistrar(@NotNull LifecycleEventManager lifecycleManager) {
//        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
//            Commands registrar = event.registrar();
//
//            for (BukkitCommandDefinition definition : definitions) {
//                for (String label : builder.labels(definition)) {
//                    registrar.register(builder.build(definition, label), definition.getDescription(), List.of());
//                }
//            }
//        });
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        registeredCommands.add(definition);
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
        return Type.PAPER_BRIGADIER;
    }

}
