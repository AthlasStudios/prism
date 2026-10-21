package dev.gurwi.prism.velocity.executor;

import com.velocitypowered.api.command.CommandSource;

public interface GenericCommandExecutor extends VelocityCommandExecutor<CommandSource> {

    @Override
    default Class<CommandSource> getSourceType() {
        return CommandSource.class;
    }

}
