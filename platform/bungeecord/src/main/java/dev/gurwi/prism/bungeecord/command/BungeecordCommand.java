package dev.gurwi.prism.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.command.builder.AbstractTreeCommandBuilder;
import dev.gurwi.prism.command.builder.CommandChildrenFactory;
import dev.gurwi.prism.bungeecord.argument.base.BungeecordArgument;
import dev.gurwi.prism.bungeecord.executor.BungeecordCommandExecutor;
import dev.gurwi.prism.bungeecord.executor.BungeecordExecutorContainerBuilder;
import dev.gurwi.prism.bungeecord.permission.BungeecordPermission;
import dev.gurwi.prism.bungeecord.requirement.BungeecordUsageRequirementContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BungeecordCommand extends AbstractTreeCommandBuilder<
        BungeecordCommand,
        BungeecordCommandDefinition,
        CommandSender,
        BungeecordCommandExecutor<?>,
        BungeecordArgument<?, ?>,
        BungeecordPermission
> implements BungeecordExecutorContainerBuilder<BungeecordCommand>, BungeecordUsageRequirementContainer<BungeecordCommand> {

    public BungeecordCommand(String name, List<String> aliases) {
        super(name, aliases);
    }

    public BungeecordCommand(String name, String... aliases) {
        super(name, aliases);
    }

    public BungeecordCommand withPermission(String... permissions) {
        withPermission(BungeecordPermission.of(permissions));
        return self();
    }


    @Override
    public @NotNull BungeecordCommandDefinition build(@Nullable BungeecordCommandDefinition parent) {
        return new BungeecordCommandDefinition(
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
                argumentTreeBranches
        );
    }

}
