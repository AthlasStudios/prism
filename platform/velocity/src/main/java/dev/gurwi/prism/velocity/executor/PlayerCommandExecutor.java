package dev.gurwi.prism.velocity.executor;

import com.velocitypowered.api.proxy.Player;

@FunctionalInterface
public interface PlayerCommandExecutor extends VelocityCommandExecutor<Player> {

    @Override
    default Class<Player> getSourceType() {
        return Player.class;
    }

}
