package dev.gurwi.prism.brigadier.minecraft;

import dev.gurwi.prism.brigadier.BrigadierTreeBuilder;
import dev.gurwi.prism.brigadier.minecraft.mapping.DefaultMappings;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BrigadierCommandRegistrar implements BukkitCommandRegistrar {

    private final Set<BukkitCommandDefinition> definitions = ConcurrentHashMap.newKeySet();

    private final MinecraftCommandDispatcherProvider dispatcherProvider;
    private final BrigadierDefinitionAdapter adapter;

    public BrigadierCommandRegistrar(MinecraftCommandDispatcherProvider dispatcherProvider) {
        DefaultMappings.registerDefaults();

        this.dispatcherProvider = dispatcherProvider;
        this.adapter = new BrigadierDefinitionAdapter();
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        BrigadierTreeBuilder<MinecraftBrigadierSource, BukkitCommandDefinition> builder = adapter.getBuilder();

        for (String label : adapter.getBuilder().labels(definition)) {
            dispatcherProvider.register(builder.build(definition, label), definition.getDescription(), Collections.emptyList());
        }

        definitions.add(definition);
    }

    @Override
    public void unregister(BukkitCommandDefinition definition) {

    }

    @Override
    public @Unmodifiable Set<BukkitCommandDefinition> getRegisteredCommands() {
        return Collections.unmodifiableSet(definitions);
    }

}
