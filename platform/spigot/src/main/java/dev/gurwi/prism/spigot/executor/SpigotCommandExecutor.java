package dev.gurwi.prism.spigot.executor;

import dev.gurwi.prism.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface SpigotCommandExecutor<T extends CommandSender> extends CommandExecutor<T> {

    default T cast(CommandSender sender) {
        return getSourceType().cast(sender);
    }

}
