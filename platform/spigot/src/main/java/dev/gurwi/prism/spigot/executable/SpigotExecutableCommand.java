package dev.gurwi.prism.spigot.executable;

import dev.gurwi.prism.executable.CommandDefinition;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpigotExecutableCommand extends Command  {

    private final CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> commandDefinition;

    public SpigotExecutableCommand(@NotNull CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> commandDefinition) {
        super(commandDefinition.getName());
        this.commandDefinition = commandDefinition;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }

    public CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> getCommandDefinition() {
        return commandDefinition;
    }

}
