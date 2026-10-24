package dev.gurwi.prism.bungeecord.executor;

import net.md_5.bungee.api.CommandSender;

public interface GenericCommandExecutor extends BungeecordCommandExecutor<CommandSender> {

    @Override
    default Class<CommandSender> getSourceType() {
        return CommandSender.class;
    }

}
