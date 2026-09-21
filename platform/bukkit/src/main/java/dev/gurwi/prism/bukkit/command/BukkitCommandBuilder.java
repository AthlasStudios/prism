package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.command.executor.*;
import dev.gurwi.prism.command.AbstractCommandBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class BukkitCommandBuilder<Impl extends BukkitCommandBuilder<Impl>> extends AbstractCommandBuilder<Impl, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>> {

    public BukkitCommandBuilder(String name, List<String> aliases, String description) {
        super(name, aliases, description);
    }

    public BukkitCommandBuilder(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    public BukkitCommandBuilder(String name, String... aliases) {
        super(name, aliases);
    }

    public Impl executes(GenericCommandExecutor executor) {
        return executes(GenericCommandExecutor.class, executor);
    }

    public Impl executesPlayer(PlayerCommandExecutor executor) {
        return executes(PlayerCommandExecutor.class, executor);
    }

    public Impl executesConsole(ConsoleCommandExecutor executor) {
        return executes(ConsoleCommandExecutor.class, executor);
    }

    public Impl executesRemoteConsole(RemoteConsoleCommandExecutor executor) {
        return executes(RemoteConsoleCommandExecutor.class, executor);
    }

    public Impl executesBlock(BlockCommandExecutor executor) {
        return executes(BlockCommandExecutor.class, executor);
    }

    @Override
    public BukkitCommandDefinition build() {
        return new BukkitCommandDefinition(name, aliases, description, requirement, requiredArguments, optionalArguments, executors);
    }

}
