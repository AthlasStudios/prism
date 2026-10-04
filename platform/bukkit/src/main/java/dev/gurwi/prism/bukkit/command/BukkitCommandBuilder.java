package dev.gurwi.prism.bukkit.command;

import dev.gurwi.prism.bukkit.argument.BukkitArgument;
import dev.gurwi.prism.bukkit.command.error.LegacyCommandExecutionErrorHandler;
import dev.gurwi.prism.bukkit.command.executor.*;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import dev.gurwi.prism.command.AbstractCommandBuilder;
import dev.gurwi.prism.command.CommandChildrenFactory;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BukkitCommandBuilder<Impl extends BukkitCommandBuilder<Impl>> extends AbstractCommandBuilder<Impl, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission> {

    protected LegacyCommandExecutionErrorHandler legacyCommandErrorHandler = LegacyCommandExecutionErrorHandler.defaultHandler();

    public BukkitCommandBuilder(String name, List<String> aliases, String description) {
        super(name, aliases, description);
    }

    public BukkitCommandBuilder(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    public BukkitCommandBuilder(String name, String... aliases) {
        super(name, aliases);
    }

    public <Sender extends CommandSender> Impl withRequirement(Class<Sender> type, UsageRequirement<Sender> requirement) {
        return withRequirement(sender ->
                !type.isInstance(sender) || requirement.test(type.cast(sender))
        );
    }

    public Impl withPermissions(String... permissions) {
        withPermissions(BukkitPermission.of(permissions));
        return self();
    }

    public Impl withPlayerRequirement(UsageRequirement<Player> requirement) {
        return withRequirement(Player.class, requirement);
    }

    public Impl withConsoleRequirement(UsageRequirement<ConsoleCommandSender> requirement) {
        return withRequirement(ConsoleCommandSender.class, requirement);
    }

    public Impl withRemoteConsoleRequirement(UsageRequirement<RemoteConsoleCommandSender> requirement) {
        return withRequirement(RemoteConsoleCommandSender.class, requirement);
    }

    public Impl withProxyRequirement(UsageRequirement<ProxiedCommandSender> requirement) {
        return withRequirement(ProxiedCommandSender.class, requirement);
    }

    public Impl withBlockRequirement(UsageRequirement<BlockCommandSender> requirement) {
        return withRequirement(BlockCommandSender.class, requirement);
    }

    public Impl executes(GenericCommandExecutor executor) {
        return executes(GenericCommandExecutor.class, executor);
    }

    public Impl executesPlayer(PlayerCommandExecutor executor) {
        return executes(PlayerCommandExecutor.class, executor);
    }

    public Impl executesConsole(ConsoleCommandExecutor executor) {
        return executes(ConsoleCommandExecutor.class, executor);
    }

    public Impl executesRemoteConsole(RemoteConsoleCommandExecutor executor) {
        return executes(RemoteConsoleCommandExecutor.class, executor);
    }

    public Impl executesProxy(ProxyCommandExecutor executor) {
        return executes(ProxyCommandExecutor.class, executor);
    }

    public Impl executesBlock(BlockCommandExecutor executor) {
        return executes(BlockCommandExecutor.class, executor);
    }

    public Impl replaceErrorHandler(LegacyCommandExecutionErrorHandler handler) {
        this.legacyCommandErrorHandler = handler;
        return self();
    }

    @Override
    public BukkitCommandDefinition build(@Nullable BukkitCommandDefinition parent) {
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
                legacyCommandErrorHandler
        );
    }

}
