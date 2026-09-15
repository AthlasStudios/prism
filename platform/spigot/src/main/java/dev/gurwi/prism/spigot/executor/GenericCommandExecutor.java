package dev.gurwi.prism.spigot.executor;

import dev.gurwi.prism.argument.CommandArguments;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface GenericCommandExecutor extends SpigotCommandExecutor<CommandSender> {

    @Override
    void run(CommandSender sender, CommandArguments args);

}
