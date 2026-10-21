package dev.gurwi.prism.velocity.executor;

import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.command.builder.ExecutorContainerBuilder;

public interface VelocityExecutorContainerBuilder<Impl> extends ExecutorContainerBuilder<Impl, VelocityCommandExecutor<? extends CommandSource>> {

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
