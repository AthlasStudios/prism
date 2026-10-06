package dev.gurwi.prism.paper;

import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import dev.gurwi.prism.bukkit.registrar.LegacyCommandRegistrar;
import dev.gurwi.prism.paper.registrar.PaperCommandRegistrar;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public class PaperCommandsRegistry implements BukkitCommandsRegistry<PaperPlatform> {

    private final PaperPlatform platform;
    private final BukkitCommandRegistrar registrar;

    public PaperCommandsRegistry(PaperPlatform platform) {
        this.platform = platform;
        this.registrar = getRegistrar();
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        if (getRegisteredCommands().contains(definition)) throw new IllegalStateException("Command " + definition.getName() + " is already registered!");
        registrar.register(definition);
    }

    @Override
    public void unregister(BukkitCommandDefinition definition) {
        registrar.unregister(definition);
    }

    @Override
    public @Unmodifiable Set<BukkitCommandDefinition> getRegisteredCommands() {
        return Set.of();
    }

    @Override
    public PaperPlatform getPlatform() {
        return platform;
    }

    @Contract(" -> new")
    private @NotNull BukkitCommandRegistrar getRegistrar() {
        return new PaperCommandRegistrar(platform.getPlugin());
//        try {
//            return new LegacyCommandRegistrar(platform.getPlugin());
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
    }

}
