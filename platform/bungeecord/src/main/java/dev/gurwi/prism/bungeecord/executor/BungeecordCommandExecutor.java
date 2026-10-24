package dev.gurwi.prism.bungeecord.executor;

import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bungeecord.command.BungeecordCommandDefinition;
import dev.gurwi.prism.command.CommandExecutor;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface BungeecordCommandExecutor<Source extends CommandSender> extends CommandExecutor<Source, BungeecordCommandDefinition> {

    Class<Source> getSourceType();

    default boolean accepts(@NotNull CommandSender sender) {
        return getSourceType().isInstance(sender);
    }

    default void tryInvoke(@NotNull CommandSender sender, CommandArguments<BungeecordCommandDefinition, ? super Source> args) {
        if (!accepts(sender)) return;
        run(getSourceType().cast(sender), args);
    }

}
