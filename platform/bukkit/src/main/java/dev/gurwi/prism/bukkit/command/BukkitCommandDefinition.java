package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.command.AbstractTreeCommandDefinition;
import dev.gurwi.prism.command.builder.CommandChildrenFactory;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BukkitCommandDefinition extends AbstractTreeCommandDefinition<BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission> {

    private final String usageString = buildUsageString();

    private final LegacyCommandExecutionErrorHandler legacyCommandErrorHandler;

    protected BukkitCommandDefinition(@Nullable BukkitCommandDefinition parent, String name, List<String> aliases, String description, UsageRequirement<CommandSender> requirement, Set<BukkitPermission> bukkitPermissions, List<BukkitArgument<?, ?>> requiredArguments, List<BukkitArgument<?, ?>> optionalArguments, Map<Class<? extends BukkitCommandExecutor<? extends CommandSender>>, BukkitCommandExecutor<? extends CommandSender>> executors, @NotNull CommandChildrenFactory<BukkitCommandDefinition> childrenFactory, List<AbstractTreeArgument<?, ?, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission, ?>> argumentTreeBranches, LegacyCommandExecutionErrorHandler legacyCommandErrorHandler) {
        super(parent, name, aliases, description, requirement, bukkitPermissions, requiredArguments, optionalArguments, executors, childrenFactory, argumentTreeBranches);
        this.legacyCommandErrorHandler = legacyCommandErrorHandler;
    }

    protected BukkitCommandDefinition(String name, List<String> aliases, String description, UsageRequirement<CommandSender> requirement, Set<BukkitPermission> bukkitPermissions, List<BukkitArgument<?, ?>> requiredArguments, List<BukkitArgument<?, ?>> optionalArguments, Map<Class<? extends BukkitCommandExecutor<? extends CommandSender>>, BukkitCommandExecutor<? extends CommandSender>> executors, CommandChildrenFactory<BukkitCommandDefinition> childrenFactory, List<AbstractTreeArgument<?, ?, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission, ?>> argumentTreeBranches, LegacyCommandExecutionErrorHandler legacyCommandErrorHandler) {
        super(name, aliases, description, requirement, bukkitPermissions, requiredArguments, optionalArguments, executors, childrenFactory, argumentTreeBranches);
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

    public @NotNull String buildUsageString(CommandSender sender) {
        return buildHierarchy().stream()
                .filter(definition -> definition.canExecute(sender))
                .flatMap(definition -> Stream.concat(
                        Stream.of(definition.getName()),
                        Stream.concat(
                                definition.getRequiredArguments().stream()
                                        .filter(argument -> argument.getRequirement().test(sender))
                                        .map(argument -> "<" + argument.getName() + ">"),
                                definition.getOptionalArguments().stream()
                                        .filter(argument -> argument.getRequirement().test(sender))
                                        .map(argument -> "[<" + argument.getName() + ">]")
                        )
                ))
                .collect(Collectors.joining(" "));
    }

    private @NotNull String buildUsageString() {
        return buildHierarchy().stream()
                .flatMap(definition -> Stream.concat(
                        Stream.of(definition.getName()),
                        Stream.concat(
                                definition.getRequiredArguments().stream()
                                        .map(argument -> "<" + argument.getName() + ">"),
                                definition.getOptionalArguments().stream()
                                        .map(argument -> "[<" + argument.getName() + ">]")
                        )
                ))
                .collect(Collectors.joining(" "));
    }

    private @NotNull @Unmodifiable List<BukkitCommandDefinition> buildHierarchy() {
        Deque<BukkitCommandDefinition> hierarchy = new ArrayDeque<>();

        Optional<BukkitCommandDefinition> current = Optional.of(this);

        while (current.isPresent()) {
            BukkitCommandDefinition definition = current.get();
            hierarchy.addFirst(definition);
            current = definition.getParent();
        }

        return List.copyOf(hierarchy);
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return hasExecutor(sender) && super.canExecute(sender);
    }

}
