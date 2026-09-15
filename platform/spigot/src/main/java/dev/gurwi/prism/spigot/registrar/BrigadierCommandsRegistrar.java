package dev.gurwi.prism.spigot.registrar;

import dev.gurwi.prism.brigadier.BrigadierCommandDispatcher;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BrigadierCommandsRegistrar extends CommandsRegistrar<CommandDefinition<?, ?>> {

    private final BrigadierCommandDispatcher<CommandSender> dispatcher;

    public BrigadierCommandsRegistrar(JavaPlugin plugin) throws NoSuchFieldException, IllegalAccessException {
        super(plugin);
        this.dispatcher = ReflectionUtil.accessField(plugin.getServer(), "brigadierCommandDispatcher");
    }

}
