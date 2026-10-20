package dev.gurwi.prism.spigot;

import dev.gurwi.prism.bukkit.BukkitCommandsRegistry;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.registrar.BukkitCommandRegistrar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public class SpigotCommandsRegistry implements BukkitCommandsRegistry<SpigotPlatform> {

    private final SpigotPlatform platform;
    private final BukkitCommandRegistrar registrar;

    public SpigotCommandsRegistry(@NotNull SpigotPlatform platform) {
        this.platform = platform;
        this.registrar = createRegistrar(platform);

        platform.getPlugin().getLogger().info(
                "[prism] Command registration initialized using " + registrar.getType().name() + "."
        );
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
        return registrar.getRegisteredCommands();
    }

    @Override
    public SpigotPlatform getPlatform() {
        return platform;
    }

}
