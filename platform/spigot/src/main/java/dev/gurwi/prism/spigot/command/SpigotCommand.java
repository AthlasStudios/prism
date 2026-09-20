package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.bukkit.argument.Argument;
import dev.gurwi.prism.command.definition.CommandDefinition;
import dev.gurwi.prism.spigot.SpigotCommandsRegistry;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;

import java.util.List;
import java.util.Map;

public class SpigotCommand extends CommandDefinition<SpigotCommand, SpigotPlatform, SpigotCommandsRegistry, SpigotCommandExecutor<?>, Argument<?, ?>> {

    public SpigotCommand(String name, String[] aliases, List<Argument<?, ?>> requiredArguments, List<Argument<?, ?>> optionalArguments, Map<Class<? extends SpigotCommandExecutor<?>>, SpigotCommandExecutor<?>> executors) {
        super(name, aliases, requiredArguments, optionalArguments, executors);
    }

    @Override
    public SpigotCommand instance() {
        return this;
    }

}
