package dev.gurwi.prism.spigot;

import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import dev.gurwi.prism.bukkit.registrar.LegacyCommandRegistrar;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public class SpigotCommandsRegistry implements BukkitCommandsRegistry<SpigotPlatform> {

    private final SpigotPlatform platform;
    private final BukkitCommandRegistrar registrar;

    public SpigotCommandsRegistry(SpigotPlatform platform) {
        this.platform = platform;
        this.registrar = getRegistrar();
    }

    @Override
    public void register(BukkitCommandDefinition definition) {
        registrar.register(definition);
    }

    @Override
    public void unregister(BukkitCommandDefinition definition) {
        registrar.unregister(definition);
    }

    @Override
    public @Unmodifiable Set<BukkitCommandDefinition> getRegisteredCommands() {
        return registrar.getRegisteredCommands();
    }

    @Contract(" -> new")
    private @NotNull BukkitCommandRegistrar getRegistrar() {
        try {
            return new LegacyCommandRegistrar(platform.getPlugin());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpigotPlatform getPlatform() {
        return platform;
    }

}
