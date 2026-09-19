package dev.gurwi.prism.spigot.executor;

import org.bukkit.command.BlockCommandSender;

@FunctionalInterface
public interface BlockCommandExecutor extends SpigotCommandExecutor<BlockCommandSender> {

    @Override
    default Class<BlockCommandSender> getSourceType() {
        return BlockCommandSender.class;
    }

}
