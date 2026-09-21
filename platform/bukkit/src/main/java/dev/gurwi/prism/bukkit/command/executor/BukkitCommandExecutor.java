package dev.gurwi.prism.bukkit.command.executor;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface BukkitCommandExecutor<Sender extends CommandSender> extends CommandExecutor<Sender> {

    Class<Sender> getSourceType();

    default void tryInvoke(@NotNull CommandSender sender, CommandArguments args) {
        if (!getSourceType().isInstance(sender)) return;
        run(getSourceType().cast(sender), args);
    }

}
