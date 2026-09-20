package dev.gurwi.prism.spigot;

import dev.gurwi.prism.brigadier.PrismBrigadier;
import dev.gurwi.prism.CommandsRegistry;
import dev.gurwi.prism.spigot.command.Command;
import dev.gurwi.prism.spigot.command.SpigotCommand;
import dev.gurwi.prism.spigot.registrar.BrigadierCommandsRegistrar;
import dev.gurwi.prism.spigot.registrar.CommandsRegistrar;
import dev.gurwi.prism.spigot.registrar.SpigotCommandsRegistrar;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;

public class SpigotCommandsRegistry implements CommandsRegistry<SpigotCommandsRegistry, SpigotPlatform, SpigotCommand, Command> {

    private final SpigotPlatform platform;
    private final CommandsRegistrar registrar;

    public SpigotCommandsRegistry(SpigotPlatform platform) {
        this.platform = platform;
        this.registrar = getRegistrar();
    }

    @Override
    public SpigotCommandsRegistry register(@NotNull Collection<SpigotCommand> commandDefinitions) {
        commandDefinitions.forEach(registrar::register);
        return this;
    }

    @Override
    public SpigotCommandsRegistry unregister(@NotNull Collection<SpigotCommand> commandDefinitions) {
        commandDefinitions.forEach(registrar::unregister);
        return this;
    }

    @Override
    @Unmodifiable
    public List<SpigotCommand> getRegisteredCommands() {
        return registrar.getRegisteredCommands();
    }

    @Override
    public SpigotPlatform getPlatform() {
        return platform;
    }

    @Override
    public SpigotCommandsRegistry instance() {
        return this;
    }

    @Contract(" -> new")
    private @NotNull CommandsRegistrar getRegistrar() {
        if (!platform.getConfiguration().isBrigadierDisabled() && PrismBrigadier.isCompatible()) {
            return new BrigadierCommandsRegistrar();
        }

        try {
            return new SpigotCommandsRegistrar(platform.getOwningPlugin());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
