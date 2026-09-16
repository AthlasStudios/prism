package dev.gurwi.prism.spigot.executable;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.executable.CommandDefinition;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.executor.ConsoleCommandExecutor;
import dev.gurwi.prism.spigot.executor.GenericCommandExecutor;
import dev.gurwi.prism.spigot.executor.PlayerCommandExecutor;
import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpigotExecutableCommand extends Command  {

    private final CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> commandDefinition;

    public SpigotExecutableCommand(@NotNull CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> commandDefinition) {
        super(commandDefinition.getName());
        this.commandDefinition = commandDefinition;

        setAliases(List.copyOf(commandDefinition.getAliases()));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        CommandArguments parsedArgs = new CommandArguments();

        commandDefinition.getExecutors().values().forEach(executor -> {
            if (executor instanceof PlayerCommandExecutor playerExecutor && sender instanceof Player player) {
                playerExecutor.run(player, parsedArgs);
                return;
            }

            if (executor instanceof ConsoleCommandExecutor consoleExecutor && sender instanceof ConsoleCommandSender console) {
                consoleExecutor.run(console, parsedArgs);
                return;
            }

            if (executor instanceof GenericCommandExecutor genericExecutor) {
                genericExecutor.run(sender, parsedArgs);
            }
        });

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }

    public CommandDefinition<SpigotPlatform, SpigotCommandExecutor<?>> getCommandDefinition() {
        return commandDefinition;
    }

}
