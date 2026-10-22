package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import dev.gurwi.prism.bukkit.command.executor.*;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.bukkit.requirement.BukkitRequirementContainerBuilder;
import dev.gurwi.prism.command.builder.AbstractTreeCommandBuilder;
import dev.gurwi.prism.command.builder.CommandChildrenFactory;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BukkitCommandBuilder<Impl extends BukkitCommandBuilder<Impl>> extends AbstractTreeCommandBuilder<Impl, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission> implements BukkitExecutorContainerBuilder<Impl>, BukkitRequirementContainerBuilder<Impl> {

    protected LegacyCommandExecutionErrorHandler legacyCommandErrorHandler = LegacyCommandExecutionErrorHandler.defaultHandler();

    public BukkitCommandBuilder(String name, List<String> aliases) {
        super(name, aliases);
    }

    public BukkitCommandBuilder(String name, String... aliases) {
        super(name, aliases);
    }

    public Impl withPermission(String... permissions) {
        withPermission(BukkitPermission.of(permissions));
        return self();
    }

    public Impl replaceErrorHandler(LegacyCommandExecutionErrorHandler handler) {
        this.legacyCommandErrorHandler = handler;
        return self();
    }

    @Override
    public @NotNull BukkitCommandDefinition build(@Nullable BukkitCommandDefinition parent) {
        return new BukkitCommandDefinition(
                parent,
                name,
                aliases,
                description,
                requirement,
                permissions,
                requiredArguments,
                optionalArguments,
                executors,
                CommandChildrenFactory.create(children),
                argumentTreeBranches,
                legacyCommandErrorHandler
        );
    }

}
