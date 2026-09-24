package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.command.AbstractCommandDefinition;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BukkitCommandDefinition extends AbstractCommandDefinition<CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission> {

    private final String usageString = buildUsageString();

    private final LegacyCommandExecutionErrorHandler legacyCommandErrorHandler;

    protected BukkitCommandDefinition(String name, List<String> aliases, String description, UsageRequirement<CommandSender> requirement, Set<BukkitPermission> bukkitPermissions, List<BukkitArgument<?, ?>> requiredArguments, List<BukkitArgument<?, ?>> optionalArguments, Map<Class<? extends BukkitCommandExecutor<? extends CommandSender>>, BukkitCommandExecutor<? extends CommandSender>> executors, LegacyCommandExecutionErrorHandler legacyCommandErrorHandler) {
        super(name, aliases, description, requirement, bukkitPermissions, requiredArguments, optionalArguments, executors);
        this.legacyCommandErrorHandler = legacyCommandErrorHandler;
    }

    public boolean hasExecutor(CommandSender sender) {
        return getExecutors().stream()
                .anyMatch(executor ->
                        executor.getSourceType().isInstance(sender)
                );
    }

    public String getUsageString() {
        return usageString;
    }

    public LegacyCommandExecutionErrorHandler getLegacyCommandErrorHandler() {
        return legacyCommandErrorHandler;
    }

    private @NotNull String buildUsageString() {
        return getName()
                + " "
                + Stream.concat(
                        getRequiredArguments().stream()
                                .map(argument -> "<" + argument.getName() + ">"),
                        getOptionalArguments().stream()
                                .map(argument -> "[<" + argument.getName() + ">]")
                )
                .collect(Collectors.joining(" "));
    }

}
