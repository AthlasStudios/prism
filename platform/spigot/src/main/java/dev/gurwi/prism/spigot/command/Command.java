package dev.gurwi.prism.spigot.command;

import dev.gurwi.prism.bukkit.argument.Argument;
import dev.gurwi.prism.command.AbstractPermissibleCommand;
import dev.gurwi.prism.spigot.SpigotPlatform;
import dev.gurwi.prism.spigot.executor.*;
import dev.gurwi.prism.spigot.permission.SpigotPermissionNode;
import dev.gurwi.prism.spigot.requirement.ConsoleExecutionRequirement;
import dev.gurwi.prism.spigot.requirement.PlayerExecutionRequirement;
import dev.gurwi.prism.spigot.requirement.SpigotExecutionRequirement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Command extends AbstractPermissibleCommand<Command, SpigotPlatform, SpigotCommandExecutor<?>, Argument<?, ?>, SpigotPermissionNode> {

    public Command(String name, String... aliases) {
        super(name, aliases);
    }

    public Command withPermission(String @NotNull ... permissions) {
        for (String permission : permissions) {
            withPermission(SpigotPermissionNode.of(permission));
        }

        return this;
    }

    @SafeVarargs
    public final Command withRequirement(SpigotExecutionRequirement<? extends CommandSender>... requirements) {
        this.requirements.addAll(Set.of(requirements));
        return this;
    }

    public Command withPlayerRequirement(PlayerExecutionRequirement... requirements) {
        return withRequirement(requirements);
    }

    public Command withConsoleRequirement(ConsoleExecutionRequirement... requirements) {
        return withRequirement(requirements);
    }

    public Command executes(GenericCommandExecutor executor) {
        executes(GenericCommandExecutor.class, executor);
        return this;
    }

    public Command executesPlayer(PlayerCommandExecutor executor) {
        executes(PlayerCommandExecutor.class, executor);
        return this;
    }

    public Command executesConsole(ConsoleCommandExecutor executor) {
        executes(ConsoleCommandExecutor.class, executor);
        return this;
    }

    public Command executesProxy(ProxiedCommandExecutor executor) {
        executes(ProxiedCommandExecutor.class, executor);
        return this;
    }

    @Override
    public Command instance() {
        return this;
    }

}
