package dev.gurwi.prism.spigot.requirement;

import dev.gurwi.prism.ExecutionRequirement;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SpigotExecutionRequirement<S extends CommandSender> extends ExecutionRequirement<S> {

    @Contract(pure = true)
    static @NotNull SpigotExecutionRequirement<CommandSender> player() {
        return sender -> sender instanceof Player;
    }

    static @NotNull SpigotExecutionRequirement<CommandSender> console() {
        return sender -> sender instanceof ConsoleCommandSender;
    }

    @Contract(pure = true)
    static @NotNull SpigotExecutionRequirement<CommandSender> permission(String permission) {
        return sender -> sender.hasPermission(permission);
    }

    static @NotNull SpigotExecutionRequirement<CommandSender> permission(String... permissions) {
        return sender -> {
            for (String permission : permissions) {
                if (sender.hasPermission(permission)) {
                    return true;
                }
            }
            return false;
        };
    }

    @Contract(pure = true)
    static @NotNull SpigotExecutionRequirement<CommandSender> isOp() {
        return ServerOperator::isOp;
    }

}
