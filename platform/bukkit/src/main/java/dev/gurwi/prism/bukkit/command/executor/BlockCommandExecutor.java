package dev.gurwi.prism.bukkit.command.executor;

import org.bukkit.command.BlockCommandSender;

@FunctionalInterface
public interface BlockCommandExecutor extends BukkitCommandExecutor<BlockCommandSender> {

    @Override
    default Class<BlockCommandSender> getSourceType() {
        return BlockCommandSender.class;
    }

}
