package dev.gurwi.prism.bungeecord.executor;

import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.command.builder.ExecutorContainerBuilder;

public interface BungeecordExecutorContainerBuilder<Impl> extends ExecutorContainerBuilder<Impl, BungeecordCommandExecutor<? extends CommandSender>> {

    default Impl executes(GenericCommandExecutor executor) {
        return executes(GenericCommandExecutor.class, executor);
    }

    default Impl executesPlayer(PlayerCommandExecutor executor) {
        return executes(PlayerCommandExecutor.class, executor);
    }

    default Impl executesConsole(ConsoleCommandExecutor executor) {
        return executes(ConsoleCommandExecutor.class, executor);
    }

}
