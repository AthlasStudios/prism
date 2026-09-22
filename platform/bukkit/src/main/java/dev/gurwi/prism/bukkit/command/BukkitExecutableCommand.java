package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.exception.NoExecutorException;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitExecutableCommand extends Command implements CommandExecutor, TabCompleter, PluginIdentifiableCommand {

    private final JavaPlugin plugin;
    private final BukkitCommandDefinition definition;

    public BukkitExecutableCommand(JavaPlugin plugin, @NotNull BukkitCommandDefinition definition) {
        super(definition.getName());

        this.plugin = plugin;
        this.definition = definition;

        setAliases(definition.getAliases());
        setDescription(definition.getDescription());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!hasExecutor(sender)) {
            throw new NoExecutorException(sender.getClass());
        }

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

    @Override
    public boolean testPermission(@NotNull CommandSender target) {
        return hasExecutor(target) && definition.getRequirement().canExecute(target)
                && definition.getPermissions().stream()
                .allMatch(permission -> permission.hasPermission(target));
    }

    @Override
    public boolean testPermissionSilent(@NotNull CommandSender target) {
        return testPermission(target);
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    public BukkitCommandDefinition getDefinition() {
        return definition;
    }

    private boolean hasExecutor(CommandSender sender) {
        return definition.getExecutors().stream()
                .anyMatch(executor ->
                        executor.getSourceType().isInstance(sender)
                );
    }

}
