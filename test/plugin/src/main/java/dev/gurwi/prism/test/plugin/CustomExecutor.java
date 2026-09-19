package dev.gurwi.prism.test.plugin;

import dev.gurwi.prism.spigot.executor.SpigotCommandExecutor;
import org.bukkit.command.BlockCommandSender;

@FunctionalInterface
public interface CustomExecutor extends SpigotCommandExecutor<BlockCommandSender> {

    @Override
    default Class<BlockCommandSender> getSourceType() {
        return BlockCommandSender.class;
    }

}
