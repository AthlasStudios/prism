package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.parser.LegacyArgumentsParser;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentsParseException;
import dev.gurwi.prism.exception.NoExecutorException;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
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

        try {
            CommandArguments parsedArgs = LegacyArgumentsParser.parse(
                    definition,
                    args
            );

            definition.getExecutors().forEach(executor -> executor.tryInvoke(sender, parsedArgs));

            return true;
        } catch (BukkitArgumentsParseException e) {
            definition.getLegacyCommandErrorHandler().onArgumentsParseError(definition, sender, e);
            return false;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return execute(sender, label, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        int argIndex = args.length == 0 ? 0 : args.length - 1;

        if (args.length <= definition.getRequiredArguments().size()) {
            BukkitArgument<?, ?> argument = definition.getRequiredArguments().get(argIndex);
            if (!argument.getRequirement().canExecute(sender)) return Collections.emptyList();
            return List.copyOf(argument.getSuggestionsSupplier().getSuggestions(sender));
        }

        if ((args.length - definition.getRequiredArguments().size()) <= definition.getOptionalArguments().size()) {
            BukkitArgument<?, ?> argument = definition.getOptionalArguments().get(argIndex - definition.getRequiredArguments().size());
            if (!argument.getRequirement().canExecute(sender)) return Collections.emptyList();
            return List.copyOf(argument.getSuggestionsSupplier().getSuggestions(sender));
        }

        return Collections.emptyList();
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
