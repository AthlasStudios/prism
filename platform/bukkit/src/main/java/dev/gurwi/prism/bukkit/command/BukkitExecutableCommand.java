package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitExecutableCommand extends Command implements CommandExecutor, TabCompleter {

    private final BukkitCommandDefinition definition;

    public BukkitExecutableCommand(@NotNull BukkitCommandDefinition definition) {
        super(definition.getName());
        setAliases(definition.getAliases());
        setDescription(definition.getDescription());

        this.definition = definition;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        definition.getExecutors().forEach(executor -> executor.tryInvoke(sender, new CommandArguments()));
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return execute(sender, label, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        for (BukkitArgument<?, ?> requiredArgument : definition.getRequiredArguments()) {
            requiredArgument.getSuggestionsSupplier().getSuggestions(sender);
        }

        return super.tabComplete(sender, alias, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tabComplete(sender, alias, args);
    }

    public BukkitCommandDefinition getDefinition() {
        return definition;
    }

}
