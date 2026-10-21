package dev.gurwi.prism.velocity.executor;

import com.velocitypowered.api.proxy.ConsoleCommandSource;

public interface ConsoleCommandExecutor extends VelocityCommandExecutor<ConsoleCommandSource> {

    default Class<ConsoleCommandSource> getSourceType() {
        return ConsoleCommandSource.class;
    }

}
