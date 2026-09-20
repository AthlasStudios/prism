package dev.gurwi.prism.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BukkitExecutableCommand extends Command {

    private final BukkitCommandDefinition definition;

    public BukkitExecutableCommand(@NotNull BukkitCommandDefinition definition) {
        super(definition.getName());
        setAliases(definition.getAliases());
        setDescription(definition.getDescription());

        this.definition = definition;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }

    public BukkitCommandDefinition getDefinition() {
        return definition;
    }

}
