package dev.gurwi.prism.bukkit.command.executor;

import dev.gurwi.prism.command.builder.ExecutorContainerBuilder;
import org.bukkit.command.CommandSender;

public interface BukkitExecutorContainerBuilder<Impl> extends ExecutorContainerBuilder<Impl, BukkitCommandExecutor<? extends CommandSender>> {

    default Impl executes(GenericCommandExecutor executor) {
        return executes(GenericCommandExecutor.class, executor);
    }

    default Impl executesPlayer(PlayerCommandExecutor executor) {
        return executes(PlayerCommandExecutor.class, executor);
    }

    default Impl executesConsole(ConsoleCommandExecutor executor) {
        return executes(ConsoleCommandExecutor.class, executor);
    }

    default Impl executesRemoteConsole(RemoteConsoleCommandExecutor executor) {
        return executes(RemoteConsoleCommandExecutor.class, executor);
    }

    default Impl executesProxy(ProxyCommandExecutor executor) {
        return executes(ProxyCommandExecutor.class, executor);
    }

    default Impl executesBlock(BlockCommandExecutor executor) {
        return executes(BlockCommandExecutor.class, executor);
    }

}
