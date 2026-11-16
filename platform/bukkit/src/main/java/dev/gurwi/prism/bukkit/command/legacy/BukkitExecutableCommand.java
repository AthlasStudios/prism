package dev.gurwi.prism.bukkit.command.legacy;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.argument.parser.ArgumentTokens;
import dev.gurwi.prism.bukkit.argument.parser.LegacyArgumentsParser;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.bukkit.exception.BukkitArgumentParseException;
import dev.gurwi.prism.exception.CommandExecutionException;
import dev.gurwi.prism.exception.NoExecutorException;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BukkitExecutableCommand extends BukkitCommand implements CommandExecutor, TabCompleter, PluginIdentifiableCommand {

    private final Plugin plugin;
    private final BukkitCommandDefinition definition;

    public BukkitExecutableCommand(Plugin plugin, @NotNull BukkitCommandDefinition definition) {
        super(definition.getName());

        this.plugin = plugin;
        this.definition = definition;

        definition.visitArguments((argument, embedded) -> argument.applyLegacy());

        setAliases(definition.getAliases());
        setDescription(definition.getDescription());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!testPermission(sender)) return false;

        List<Level> levels = resolve(sender, args);

        CommandArguments<BukkitCommandDefinition, CommandSender> parsed = null;
        BukkitArgument<?, ?> node = null;

        for (Level level : levels) {
            BukkitArgumentParseException invalid = LegacyFixedValues.check(sender, level.definition, level.args, level.start, args);

            if (invalid != null) {
                level.definition.getLegacyCommandErrorHandler().onArgumentsParseError(level.definition, sender, invalid);
                return false;
            }
        }

        String fullInput = args.length == 0 ? label : label + " " + String.join(" ", args);

        for (Level level : levels) {
            try {
                LegacyArgumentsParser.TreeParseResult result = LegacyArgumentsParser.parseTree(sender, level.definition, level.args, args, level.start, parsed, fullInput);

                parsed = result.arguments();
                node = result.node();
            } catch (BukkitArgumentParseException e) {
                level.definition.getLegacyCommandErrorHandler().onArgumentsParseError(level.definition, sender, e);
                return false;
            }
        }

        BukkitCommandDefinition target = levels.get(levels.size() - 1).definition;
        CommandArguments<BukkitCommandDefinition, CommandSender> finalArguments = parsed;

        Collection<BukkitCommandExecutor<? extends CommandSender>> executors = node == null ? target.getExecutors() : node.getExecutors();

        if (node == null ? !target.hasExecutor(sender) : !node.hasExecutor(sender)) {
            throw new NoExecutorException(sender.getClass());
        }

        try {
            executors.forEach(executor -> executor.tryInvoke(sender, finalArguments));
        } catch (CommandExecutionException e) {
            target.getLegacyCommandErrorHandler().onCommandExecutionError(target, sender, e);
        }

        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return execute(sender, label, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (args.length == 0 || !testPermission(sender)) return List.of();

        return LegacyTabCompleter.complete(definition, sender, alias, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tabComplete(sender, alias, args);
    }

    public @NotNull Optional<CompletableFuture<List<String>>> completeAsync(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 0 || !testPermission(sender)) return Optional.empty();

        return LegacyTabCompleter.completeAsync(definition, sender, args);
    }

    @Override
    public boolean testPermission(@NotNull CommandSender target) {
        return definition.canExecute(target);
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

    private @NotNull List<Level> resolve(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        List<Level> levels = new ArrayList<>();

        BukkitCommandDefinition current = definition;
        int position = 0;

        while (true) {
            int start = position;
            List<BukkitArgument<?, ?>> required = current.getRequiredArguments();
            List<BukkitArgument<?, ?>> optional = current.getOptionalArguments();

            int requiredTokens = 0;

            for (BukkitArgument<?, ?> argument : required) {
                requiredTokens += ArgumentTokens.consumption(argument);
            }

            if (args.length - start < requiredTokens) {
                levels.add(new Level(current, Arrays.copyOfRange(args, start, args.length), start));
                return levels;
            }

            for (BukkitArgument<?, ?> argument : required) {
                position = Math.min(ArgumentTokens.advance(argument, position, args.length), args.length);
            }

            int optionalIndex = 0;
            Optional<BukkitCommandDefinition> child = Optional.empty();

            while (position < args.length) {
                child = LegacyTabCompleter.findChild(current, sender, args[position]);

                if (child.isPresent() || optionalIndex >= optional.size()) {
                    break;
                }

                position = Math.min(ArgumentTokens.advance(optional.get(optionalIndex), position, args.length), args.length);
                optionalIndex++;
            }

            if (child.isEmpty()) {
                levels.add(new Level(current, Arrays.copyOfRange(args, start, args.length), start));
                return levels;
            }

            levels.add(new Level(current, Arrays.copyOfRange(args, start, position), start));

            current = child.get();
            position++;
        }
    }

    private static final class Level {

        private final BukkitCommandDefinition definition;
        private final String[] args;
        private final int start;

        private Level(@NotNull BukkitCommandDefinition definition, @NotNull String @NotNull [] args, int start) {
            this.definition = definition;
            this.args = args;
            this.start = start;
        }

    }

}