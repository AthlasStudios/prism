package dev.gurwi.prism.spigot.registrar;

import dev.gurwi.prism.reflection.ReflectionUtil;
import dev.gurwi.prism.spigot.executable.SpigotExecutableCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SpigotCommandsRegistrar extends CommandsRegistrar<SpigotExecutableCommand> {

    private final CommandMap commandMap;
    private final Map<String, Command> registeredCommands;

    public SpigotCommandsRegistrar(@NotNull JavaPlugin plugin) throws NoSuchFieldException, IllegalAccessException {
        super(plugin);
        this.commandMap = ReflectionUtil.accessField(plugin.getServer(), "CommandMap");
        this.registeredCommands = ReflectionUtil.accessField(commandMap, "knownCommands");
    }

    public void register(SpigotExecutableCommand command) {
        commandMap.register(plugin.getName(), command);
    }

    public void unregister(@NotNull SpigotExecutableCommand command) {
        registeredCommands.remove(command.getName());
    }

}
